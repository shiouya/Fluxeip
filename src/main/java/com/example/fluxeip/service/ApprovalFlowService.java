package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.ApprovalFlowDTO;
import com.example.fluxeip.dto.ApprovalFlowResponseDTO;
import com.example.fluxeip.dto.ExpenseApprovalStepDTO;
import com.example.fluxeip.dto.LeaveApprovalStepDTO;
import com.example.fluxeip.dto.MissingPunchApprovalStepDTO;
import com.example.fluxeip.dto.WorkAdjustApprovalStepDTO;
import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceLogs;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.ExpenseRequest;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.model.MissingPunchRequest;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.repository.ApprovalFlowRepository;
import com.example.fluxeip.repository.ApprovalStepRepository;
import com.example.fluxeip.repository.AttendanceLogsRepository;
import com.example.fluxeip.repository.AttendanceRepository;
import com.example.fluxeip.repository.AttendanceViolationsRepository;
import com.example.fluxeip.repository.DepartmentRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.ExpenseRequestRepository;
import com.example.fluxeip.repository.LeaveRequestRepository;
import com.example.fluxeip.repository.MissingPunchRequestRepository;
import com.example.fluxeip.repository.PositionRepository;
import com.example.fluxeip.repository.ScheduleRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;
import com.example.fluxeip.repository.WorkAdjustmentRequestRepository;

@Service
public class ApprovalFlowService {

	@Autowired
	private ApprovalFlowRepository approvalFlowRepository;

	@Autowired
	private ApprovalStepRepository approvalStepRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private LeaveRequestRepository leaveRequestRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private PositionRepository positionRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private WorkAdjustmentRequestRepository adjustmentRequestRepository;

	@Autowired
	private FileService fileService;

	@Autowired
	private MissingPunchRequestRepository missingPunchRequestRepository;

	@Autowired
	private ExpenseRequestRepository expenseRequestRepository;
	
	@Autowired
	private NotifyService notifyService;

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private AttendanceLogsRepository attendanceLogsRepository;

	@Autowired
	private AttendanceViolationsRepository attendanceViolationsRepository;

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private ScheduleRepository scheduleRepository;

	// 查詢員工待審核的請假單
	@Transactional
	public List<LeaveApprovalStepDTO> getPendingApprovalSteps(Integer approverId) {
		// 查詢待審核的 ApprovalStep
		List<ApprovalStep> pendingSteps = approvalStepRepository.findPendingApprovalSteps(approverId, "待審核");

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return pendingSteps.stream().filter(step -> "leave_type".equals(step.getFlow().getRequestType().getCategory())) // ✅
																														// 篩選出請假類型
				.map(step -> {
					Integer requestId = step.getRequestId();
					System.out.println("蝦" + requestId);

					// 避免 Optional 取值時發生錯誤
					LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
							.orElseThrow(() -> new NoSuchElementException("找不到 ID 為 " + requestId + " 的請假申請"));

					// 構建 DTO
					return new LeaveApprovalStepDTO(step.getId(), requestId, leaveRequest.getEmployee().getEmployeeId(),
							leaveRequest.getEmployee().getEmployeeName(), leaveRequest.getLeaveType().getTypeName(),
							leaveRequest.getStartDatetime(), leaveRequest.getEndDatetime(),
							leaveRequest.getLeaveHours(), leaveRequest.getReason(), leaveRequest.getSubmittedAt(),
							fileService.extractOriginalFileName(leaveRequest.getAttachments()),
							leaveRequest.getAttachments(), step.getApprover().getEmployeeId(),
							step.getApprover().getEmployeeName(), step.getStatus().getStatusName(),
							step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
				}).collect(Collectors.toList());

	}

	// 啟動請假簽核
	@Transactional
	public void startLeaveApprovalProcess(LeaveRequest leaveRequest) {

		// 取得員工的職位
		Integer positionId = leaveRequest.getEmployee().getPosition().getPositionId();
		Integer requestTypeId = leaveRequest.getLeaveType().getId();

		// 查詢對應的第一步驟簽核流程
		Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository.findApprovalFlow(positionId, requestTypeId, 1)
				.stream().findFirst();
		System.out.println(firstStepFlowOpt.get().getFlowName());
		// 先查詢員工專屬的簽核流程
		Optional<ApprovalFlow> employeeFlow = approvalFlowRepository
				.findFirstStepByEmployee(leaveRequest.getEmployee().getEmployeeId(), requestTypeId);

		if (employeeFlow.isPresent()) {
			firstStepFlowOpt = employeeFlow;
		}

		if (!firstStepFlowOpt.isPresent()) {
			throw new RuntimeException("未找到對應的簽核流程");
		}
		ApprovalFlow firstStepFlow = firstStepFlowOpt.get();

		// 找到該部門中符合該職位的第一位簽核人
		Optional<Employee> approverOpt = employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						leaveRequest.getEmployee().getDepartment(),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst();

		// 如果找不到符合條件的簽核人，進行二次查詢以處理特殊情況
		approverOpt = approverOpt.or(() -> employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						departmentRepository.findByDepartmentName("總經理部")
								.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst());

		// 如果二次查詢仍然找不到簽核人，拋出異常
		Employee approver = approverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的簽核人，請確認設定"));

		// 打印簽核人的姓名（可用於調試）
		System.out.println(approver.getEmployeeName());

		// 創建簽核步驟
		ApprovalStep approvalStep = new ApprovalStep();
		approvalStep.setFlow(firstStepFlow);

		approvalStep.setRequestId(leaveRequest.getId()); // leaveRequest 是 BaseRequest 類型的子類型

		approvalStep.setCurrentStep(1);
		approvalStep.setApprover(approver);
		approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態").orElse(null));
		approvalStep.setUpdatedAt(LocalDateTime.now());

		// 儲存簽核步驟
		approvalStepRepository.save(approvalStep);
		
	    // 發送通知給第一關審核人
		String typeName = leaveRequest.getLeaveType().getTypeName(); 
		String employeeName = leaveRequest.getEmployee().getEmployeeName();

		String message = "您有一筆新的請假申請待審核：員工 " + employeeName + " 的" + typeName + " 假單。";
		notifyService.sendNotification(approver.getEmployeeId(), message);
	}

	// 請假簽核中
	@Transactional
	public String approveLeaveRequest(Integer approvalStepId, Integer approverUserId, String statusName,
			String comment) {
		ApprovalStep step = approvalStepRepository.findById(approvalStepId)
				.orElseThrow(() -> new RuntimeException("簽核步驟不存在"));

		// 檢查是否是正確的審核人
		if (!step.getApprover().getEmployeeId().equals(approverUserId)) {
			return "你沒有權限審核這個請假單";
		}

		// 更新當前審核步驟狀態
		step.setStatus(statusRepository.findByStatusNameAndStatusType(statusName, "表單狀態")
				.orElseThrow(() -> new RuntimeException("狀態不存在")));
		step.setComment(comment);
		step.setUpdatedAt(LocalDateTime.now());
		approvalStepRepository.save(step);

		// ** 如果否決，直接更新請假單狀態**
		if ("未核准".equals(step.getStatus().getStatusName())) {
			Integer requestId = step.getRequestId();
			Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(requestId);
			LeaveRequest leaveRequest = leaveRequestOpt.get();
			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("未核准", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));

			leaveRequestRepository.save(leaveRequest);
						
			 // 通知申請人審核失敗
	        String message = "您的請假申請《" + leaveRequest.getLeaveType().getTypeName() + "》未通過審核。";
	        notifyService.sendNotification(leaveRequest.getEmployee().getEmployeeId(), message);		
					
			return "已否決請假單";
		}

		// ** 否則進入「核准」流程**
		ApprovalFlow currentFlow = step.getFlow();

		// 取得下一步流程
		ApprovalFlow nextStepFlow = currentFlow.getNextStep();
		if (nextStepFlow == null) {
			// 如果沒有下一步，直接完成簽核流程
			Integer requestId = step.getRequestId();
			Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(requestId);
			LeaveRequest leaveRequest = leaveRequestOpt.get();
			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			leaveRequestRepository.save(leaveRequest);
			
			// 通知申請人審核通過
	        String message = "您的請假申請《" + leaveRequest.getLeaveType().getTypeName() + "》已通過審核。";
	        notifyService.sendNotification(leaveRequest.getEmployee().getEmployeeId(), message);

			return "簽核成功";
		}

		// 繼續處理下一步流程
		Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findById(nextStepFlow.getId());
		if (nextFlowOpt.isPresent()) {
			// 若有下一步驟，新增下一個 `ApprovalStep`
			Integer requestId = step.getRequestId();
			Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(requestId);
			LeaveRequest leaveRequest = leaveRequestOpt.get();
			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("審核中", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			leaveRequestRepository.save(leaveRequest);

			// 獲取下一個審核人
			ApprovalFlow nextFlow = nextFlowOpt.get();
			Employee nextApprover = employeeRepository.findTopByPositionAndDepartmentAndStatus(
					nextFlow.getApproverPosition(), step.getApprover().getDepartment(),
					statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
					.orElseGet(() -> {
						Optional<Employee> fallbackApproverOpt = employeeRepository
								.findTopByPositionAndDepartmentAndStatus(nextFlow.getApproverPosition(),
										departmentRepository.findByDepartmentName("總經理部")
												.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
										statusRepository.findByStatusName("在職")
												.orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
								.stream().findFirst();

						return fallbackApproverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的審核人，請確認設定"));
					});

			// 新增下一步的 ApprovalStep
			ApprovalStep nextStep = new ApprovalStep();
			nextStep.setFlow(nextFlow);
			nextStep.setRequestId(requestId); // 設定為父類型 BaseRequest
			nextStep.setCurrentStep(nextFlow.getStepOrder());
			nextStep.setApprover(nextApprover);
			nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			nextStep.setUpdatedAt(LocalDateTime.now());

			approvalStepRepository.save(nextStep);
			
			// 通知下一位審核人
			String typeName = leaveRequest.getLeaveType().getTypeName(); 
			String employeeName = leaveRequest.getEmployee().getEmployeeName();

			String message = "您有一筆新的請假申請待審核：員工 " + employeeName + " 的" + typeName + " 假單。";
			notifyService.sendNotification(nextApprover.getEmployeeId(), message);
			
			
		} else {
			// 若沒有下一步，代表簽核完成，更新請假單狀態
			Integer requestId = step.getRequestId();
			Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(requestId);
			LeaveRequest leaveRequest = leaveRequestOpt.get();
			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			leaveRequestRepository.save(leaveRequest);
			
			// 通知申請人審核通過
			String message = "您的請假申請《" + leaveRequest.getLeaveType().getTypeName() + "》已通過審核。";
			notifyService.sendNotification(leaveRequest.getEmployee().getEmployeeId(), message);

		}

		return "簽核成功";
	}

	// 查詢員工待審核的加減班單
	@Transactional
	public List<WorkAdjustApprovalStepDTO> getPendingWorkAdjustApprovalSteps(Integer approverId) {
		// 查詢待審核的 ApprovalStep
		List<ApprovalStep> pendingSteps = approvalStepRepository.findPendingApprovalSteps(approverId, "待審核");

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return pendingSteps.stream()
				.filter(step -> "work_adjustment_type".equals(step.getFlow().getRequestType().getCategory()))
				.map(step -> {
					Integer requestId = step.getRequestId();
					System.out.println("魚" + requestId);
					// 避免 Optional 取值時發生錯誤
					WorkAdjustmentRequest request = adjustmentRequestRepository.findById(requestId)
							.orElseThrow(() -> new NoSuchElementException("找不到 ID 為 " + requestId + " 的加減班申請"));

					// 構建 ApprovalStepDTO
					return new WorkAdjustApprovalStepDTO(step.getId(), requestId, request.getEmployee().getEmployeeId(),
							request.getEmployee().getEmployeeName(), request.getAdjustmentType().getTypeName(),
							request.getAdjustmentDate(), request.getHours(), request.getReason(),
							request.getSubmittedAt(), step.getApprover().getEmployeeId(),
							step.getApprover().getEmployeeName(), step.getStatus().getStatusName(),
							step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
				}).collect(Collectors.toList());
	}

	// 啟動加減班簽核
	@Transactional
	public void startWorkAdjustApprovalProcess(WorkAdjustmentRequest workAdjustmentRequest) {
		// 取得員工的職位
		Integer positionId = workAdjustmentRequest.getEmployee().getPosition().getPositionId();
		Integer requestTypeId = workAdjustmentRequest.getAdjustmentType().getId();

		// 查詢對應的第一步驟簽核流程
		Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository.findApprovalFlow(positionId, requestTypeId, 1)
				.stream().findFirst();
		System.out.println(firstStepFlowOpt.get().getFlowName());
		// 先查詢員工專屬的簽核流程
		Optional<ApprovalFlow> employeeFlow = approvalFlowRepository
				.findFirstStepByEmployee(workAdjustmentRequest.getEmployee().getEmployeeId(), requestTypeId);

		if (employeeFlow.isPresent()) {
			firstStepFlowOpt = employeeFlow;
		}

		if (!firstStepFlowOpt.isPresent()) {
			throw new RuntimeException("未找到對應的簽核流程");
		}
		ApprovalFlow firstStepFlow = firstStepFlowOpt.get();

		// 找到該部門中符合該職位的第一位簽核人
		Optional<Employee> approverOpt = employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						workAdjustmentRequest.getEmployee().getDepartment(),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst();

		// 如果找不到符合條件的簽核人，進行二次查詢以處理特殊情況
		approverOpt = approverOpt.or(() -> employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						departmentRepository.findByDepartmentName("總經理部")
								.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst());

		// 如果二次查詢仍然找不到簽核人，拋出異常
		Employee approver = approverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的簽核人，請確認設定"));

		// 打印簽核人的姓名（可用於調試）
		System.out.println(approver.getEmployeeName());

		// 創建簽核步驟
		ApprovalStep approvalStep = new ApprovalStep();
		approvalStep.setFlow(firstStepFlow);

		// 使用 BaseRequest 類型來設定請求
		approvalStep.setRequestId(workAdjustmentRequest.getId()); // leaveRequest 是 BaseRequest 類型的子類型

		approvalStep.setCurrentStep(1);
		approvalStep.setApprover(approver);
		approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態").orElse(null));
		approvalStep.setUpdatedAt(LocalDateTime.now());

		// 儲存簽核步驟
		approvalStepRepository.save(approvalStep);

		// 發送通知：根據加班或減班顯示
		String typeName = workAdjustmentRequest.getAdjustmentType().getTypeName(); // 例如 "加班" 或 "減班"
		String employeeName = workAdjustmentRequest.getEmployee().getEmployeeName();

		String message = "您有一筆新的"+ typeName +"申請待審核：員工" + employeeName + "的" + typeName +"單。";

		notifyService.sendNotification(approver.getEmployeeId(), message);

	}

	// 加減班簽核中
	@Transactional
	public String approveWorkAdjustmentRequest(Integer approvalStepId, Integer approverUserId, String statusName,
			String comment) {
		ApprovalStep step = approvalStepRepository.findById(approvalStepId)
				.orElseThrow(() -> new RuntimeException("簽核步驟不存在"));

		// 檢查是否是正確的審核人
		if (!step.getApprover().getEmployeeId().equals(approverUserId)) {
			return "你沒有權限審核這個請假單";
		}

		// 更新當前審核步驟狀態
		step.setStatus(statusRepository.findByStatusNameAndStatusType(statusName, "表單狀態")
				.orElseThrow(() -> new RuntimeException("狀態不存在")));
		step.setComment(comment);
		step.setUpdatedAt(LocalDateTime.now());
		approvalStepRepository.save(step);

		// ** 如果否決，直接更新請假單狀態**
		if ("未核准".equals(step.getStatus().getStatusName())) {
			Integer requestId = step.getRequestId();
			Optional<WorkAdjustmentRequest> workAdjustRequestOpt = adjustmentRequestRepository.findById(requestId);
			WorkAdjustmentRequest request = workAdjustRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("未核准", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));

			adjustmentRequestRepository.save(request);
			

	        // 通知申請人
	        String typeName = request.getAdjustmentType().getTypeName(); // 加班 / 減班
	        String msg = "您的《"+ typeName +"》申請未通過審核。";
	        notifyService.sendNotification(request.getEmployee().getEmployeeId(), msg);
			        
			
			return "已否決請假單";
		}

		// ** 否則進入「核准」流程**
		ApprovalFlow currentFlow = step.getFlow();

		// 取得下一步流程
		ApprovalFlow nextStepFlow = currentFlow.getNextStep();
		if (nextStepFlow == null) {
			// 如果沒有下一步，直接完成簽核流程
			Integer requestId = step.getRequestId();
			Optional<WorkAdjustmentRequest> workAdjustRequestOpt = adjustmentRequestRepository.findById(requestId);
			WorkAdjustmentRequest request = workAdjustRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			adjustmentRequestRepository.save(request);
			
			//通知
			String typeName = request.getAdjustmentType().getTypeName();
	        String msg = "您的《"+ typeName +"》申請已通過審核。";
	        notifyService.sendNotification(request.getEmployee().getEmployeeId(), msg);
			
	     
			return "簽核成功";
		}

		// 繼續處理下一步流程
		Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findById(nextStepFlow.getId());
		if (nextFlowOpt.isPresent()) {
			// 若有下一步驟，新增下一個 `ApprovalStep`
			Integer requestId = step.getRequestId();
			Optional<WorkAdjustmentRequest> workAdjustRequestOpt = adjustmentRequestRepository.findById(requestId);
			WorkAdjustmentRequest request = workAdjustRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("審核中", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			adjustmentRequestRepository.save(request);

			// 獲取下一個審核人
			ApprovalFlow nextFlow = nextFlowOpt.get();
			Employee nextApprover = employeeRepository.findTopByPositionAndDepartmentAndStatus(
					nextFlow.getApproverPosition(), step.getApprover().getDepartment(),
					statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
					.orElseGet(() -> {
						Optional<Employee> fallbackApproverOpt = employeeRepository
								.findTopByPositionAndDepartmentAndStatus(nextFlow.getApproverPosition(),
										departmentRepository.findByDepartmentName("總經理部")
												.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
										statusRepository.findByStatusName("在職")
												.orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
								.stream().findFirst();

						return fallbackApproverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的審核人，請確認設定"));
					});

			// 新增下一步的 ApprovalStep
			ApprovalStep nextStep = new ApprovalStep();
			nextStep.setFlow(nextFlow);
			nextStep.setRequestId(requestId); // 設定為父類型 BaseRequest
			nextStep.setCurrentStep(nextFlow.getStepOrder());
			nextStep.setApprover(nextApprover);
			nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			nextStep.setUpdatedAt(LocalDateTime.now());
			
			
			//通知
			String typeName = request.getAdjustmentType().getTypeName();
		    String employeeName = request.getEmployee().getEmployeeName();
		    String msg ="您有一筆新的"+ typeName +"申請待審核：員工" +employeeName+"的"+ typeName +"申請單。";
		    notifyService.sendNotification(nextApprover.getEmployeeId(), msg);
			

			approvalStepRepository.save(nextStep);
		} else {
			// 若沒有下一步，代表簽核完成，更新請假單狀態
			Integer requestId = step.getRequestId();
			Optional<WorkAdjustmentRequest> workAdjustRequestOpt = adjustmentRequestRepository.findById(requestId);
			WorkAdjustmentRequest request = workAdjustRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			adjustmentRequestRepository.save(request);
			
			// 通知申請人審核通過
		    String message = "您的《" + request.getAdjustmentType().getTypeName() + "》申請已通過審核。";
		    notifyService.sendNotification(request.getEmployee().getEmployeeId(), message);

		}
		
		return "簽核成功";
	}

	// 查詢員工待審核的補卡單  《""》
	@Transactional
	public List<MissingPunchApprovalStepDTO> getPendingMissingPunchApprovalSteps(Integer approverId) {
		// 查詢待審核的 ApprovalStep
		List<ApprovalStep> pendingSteps = approvalStepRepository.findPendingApprovalSteps(approverId, "待審核");

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return pendingSteps.stream().filter(step -> "clock_type".equals(step.getFlow().getRequestType().getCategory()))
				.map(step -> {
					Integer requestId = step.getRequestId();
					System.out.println("花" + requestId);
					// 避免 Optional 取值時發生錯誤
					MissingPunchRequest request = missingPunchRequestRepository.findById(requestId)
							.orElseThrow(() -> new NoSuchElementException("找不到 ID 為 " + requestId + " 的加減班申請"));

					// 構建 ApprovalStepDTO
					return new MissingPunchApprovalStepDTO(step.getId(), requestId,
							request.getEmployee().getEmployeeId(), request.getEmployee().getEmployeeName(),
							request.getClockType().getTypeName(), request.getMissingDate(), request.getReason(),
							request.getSubmittedAt(), step.getApprover().getEmployeeId(),
							step.getApprover().getEmployeeName(), step.getStatus().getStatusName(),
							step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
				}).collect(Collectors.toList());
	}

	// 啟動補卡簽核
	@Transactional
	public void startMissingPunchApprovalProcess(MissingPunchRequest missingPunchRequest) {

		// 取得員工的職位
		Integer positionId = missingPunchRequest.getEmployee().getPosition().getPositionId();
		Integer requestTypeId = missingPunchRequest.getClockType().getId();

		// 查詢對應的第一步驟簽核流程
		Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository.findApprovalFlow(positionId, requestTypeId, 1)
				.stream().findFirst();
		System.out.println(firstStepFlowOpt.get().getFlowName());
		// 先查詢員工專屬的簽核流程
		Optional<ApprovalFlow> employeeFlow = approvalFlowRepository
				.findFirstStepByEmployee(missingPunchRequest.getEmployee().getEmployeeId(), requestTypeId);

		if (employeeFlow.isPresent()) {
			firstStepFlowOpt = employeeFlow;
		}

		if (!firstStepFlowOpt.isPresent()) {
			throw new RuntimeException("未找到對應的簽核流程");
		}
		ApprovalFlow firstStepFlow = firstStepFlowOpt.get();

		// 找到該部門中符合該職位的第一位簽核人
		Optional<Employee> approverOpt = employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						missingPunchRequest.getEmployee().getDepartment(),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst();

		// 如果找不到符合條件的簽核人，進行二次查詢以處理特殊情況
		approverOpt = approverOpt.or(() -> employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						departmentRepository.findByDepartmentName("總經理部")
								.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst());

		// 如果二次查詢仍然找不到簽核人，拋出異常
		Employee approver = approverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的簽核人，請確認設定"));

		// 打印簽核人的姓名（可用於調試）
		System.out.println(approver.getEmployeeName());

		// 創建簽核步驟
		ApprovalStep approvalStep = new ApprovalStep();
		approvalStep.setFlow(firstStepFlow);

		// 使用 BaseRequest 類型來設定請求
		approvalStep.setRequestId(missingPunchRequest.getId()); // leaveRequest 是 BaseRequest 類型的子類型

		approvalStep.setCurrentStep(1);
		approvalStep.setApprover(approver);
		approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態").orElse(null));
		approvalStep.setUpdatedAt(LocalDateTime.now());

		// 儲存簽核步驟
		approvalStepRepository.save(approvalStep);
		
		//補卡通知
		String employeeName = missingPunchRequest.getEmployee().getEmployeeName();
		String clockType = missingPunchRequest.getClockType().getTypeName();
		String date = missingPunchRequest.getMissingDate().toString();

		String message = "您有一筆新的補卡申請待審核：員工 " + employeeName + " 的補卡（" + date + " " + clockType + "）。";
		notifyService.sendNotification(approver.getEmployeeId(), message);

	}

	// 補卡簽核中
	@Transactional
	public String approveMissingPunchRequest(Integer approvalStepId, Integer approverUserId, String statusName,
			String comment) {
		ApprovalStep step = approvalStepRepository.findById(approvalStepId)
				.orElseThrow(() -> new RuntimeException("簽核步驟不存在"));

		// 檢查是否是正確的審核人
		if (!step.getApprover().getEmployeeId().equals(approverUserId)) {
			return "你沒有權限審核這個請假單";
		}

		// 更新當前審核步驟狀態
		step.setStatus(statusRepository.findByStatusNameAndStatusType(statusName, "表單狀態")
				.orElseThrow(() -> new RuntimeException("狀態不存在")));
		step.setComment(comment);
		step.setUpdatedAt(LocalDateTime.now());
		approvalStepRepository.save(step);

		// ** 如果否決，直接更新請假單狀態**
		if ("未核准".equals(step.getStatus().getStatusName())) {
			Integer requestId = step.getRequestId();
			MissingPunchRequest request = missingPunchRequestRepository.findById(requestId)
					.orElseThrow(() -> new RuntimeException("找不到請假申請"));
			request.setStatus(statusRepository.findByStatusNameAndStatusType("未核准", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));

			missingPunchRequestRepository.save(request);
			
			// 通知失敗
			String failMsg = "您的補卡申請《" + request.getClockType().getTypeName() + "》未通過審核。";
		    notifyService.sendNotification(request.getEmployee().getEmployeeId(), failMsg);
			
			return "已否決請假單";
		}

		// ** 否則進入「核准」流程**
		ApprovalFlow currentFlow = step.getFlow();

		// 取得下一步流程
		ApprovalFlow nextStepFlow = currentFlow.getNextStep();
		if (nextStepFlow == null) {
			// 如果沒有下一步，直接完成簽核流程
			Integer requestId = step.getRequestId();
			MissingPunchRequest request = missingPunchRequestRepository.findById(requestId)
					.orElseThrow(() -> new RuntimeException("找不到請假申請"));
			request.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			missingPunchRequestRepository.save(request);
					
			Employee employee = request.getEmployee();
			LocalDate missingDate = request.getMissingDate();

			ShiftType shiftType = scheduleRepository
					.findShiftTypeByEmployeeIdAndDate(employee.getEmployeeId(), missingDate)
					.orElseThrow(() -> new RuntimeException("找不到當日班別"));
			if (request.getClockType().getTypeName().equals("上班")) {

				// 驗證 LocalDateTime
				LocalDateTime startOfDay = missingDate.atStartOfDay();
				LocalDateTime endOfDay = startOfDay.plusDays(1);
				Optional<Type> clockInTypeOpt = typeRepository.findByTypeName("上班");
				Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee,
						startOfDay, endOfDay);
				Attendance attendance = attendanceOpt.orElseThrow(() -> new RuntimeException("指定日期無考勤紀錄或員工不存在"));
				AttendanceLogs log = new AttendanceLogs();
				log.setAttendance(attendance);
				log.setEmployee(employee);
				log.setClockType(clockInTypeOpt.orElseThrow(() -> new RuntimeException("上班打卡類型不存在")));
				log.setClockTime(missingDate.atTime(shiftType.getStartTime()));
				attendanceLogsRepository.save(log);
				Optional<AttendanceViolations> lateOpt = attendanceViolationsRepository.findByViolationType(
						typeRepository.findByTypeName("遲到").orElseThrow(() -> new RuntimeException("遲到違規類型不存在")));
				if (lateOpt.isPresent()) {
					attendanceViolationsRepository.deleteById(lateOpt.get().getId());
				}
				updateTotalHoursByClockCard(attendance, shiftType, clockInTypeOpt.get());

				// 通知成功
				String msg = "您的補卡申請《" + request.getClockType().getTypeName() + "》已通過審核。";
				notifyService.sendNotification(employee.getEmployeeId(), msg);
					
				
			} else if (request.getClockType().getTypeName().equals("下班")) {
				LocalDateTime startOfDay = missingDate.atStartOfDay();
				LocalDateTime endOfDay = startOfDay.plusDays(1);

				Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee,
						startOfDay, endOfDay);
				Attendance attendance = attendanceOpt.orElseThrow(() -> new RuntimeException("指定日期無考勤紀錄或員工不存在"));
				Optional<Type> clockOutTypeOpt = typeRepository.findByTypeName("下班");
				AttendanceLogs log = new AttendanceLogs();
				log.setAttendance(attendance);
				log.setEmployee(employee);
				log.setClockType(clockOutTypeOpt.orElseThrow(() -> new RuntimeException("下班打卡類型不存在")));
				log.setClockTime(missingDate.atTime(shiftType.getFinishTime()));
				attendanceLogsRepository.save(log);

				Optional<AttendanceViolations> earlyLeaveOpt = attendanceViolationsRepository.findByViolationType(
						typeRepository.findByTypeName("早退").orElseThrow(() -> new RuntimeException("早退違規類型不存在")));
				if (earlyLeaveOpt.isPresent()) {
					attendanceViolationsRepository.deleteById(earlyLeaveOpt.get().getId());
				}
				updateTotalHoursByClockCard(attendance, shiftType, clockOutTypeOpt.get());
				
				// 通知申請人審核通過
				String msg = "您的補卡申請《" + request.getClockType().getTypeName() + "》已通過審核。";
				notifyService.sendNotification(employee.getEmployeeId(), msg);
			}
			
			
			return "簽核成功";
		}

		// 繼續處理下一步流程
		Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findById(nextStepFlow.getId());
		if (nextFlowOpt.isPresent()) {
			// 若有下一步驟，新增下一個 `ApprovalStep`
			Integer requestId = step.getRequestId();
			MissingPunchRequest request = missingPunchRequestRepository.findById(requestId)
					.orElseThrow(() -> new RuntimeException("找不到請假申請"));
			request.setStatus(statusRepository.findByStatusNameAndStatusType("審核中", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			missingPunchRequestRepository.save(request);

			// 獲取下一個審核人
			ApprovalFlow nextFlow = nextFlowOpt.get();
			Employee nextApprover = employeeRepository.findTopByPositionAndDepartmentAndStatus(
					nextFlow.getApproverPosition(), step.getApprover().getDepartment(),
					statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
					.orElseGet(() -> {
						Optional<Employee> fallbackApproverOpt = employeeRepository
								.findTopByPositionAndDepartmentAndStatus(nextFlow.getApproverPosition(),
										departmentRepository.findByDepartmentName("總經理部")
												.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
										statusRepository.findByStatusName("在職")
												.orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
								.stream().findFirst();

						return fallbackApproverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的審核人，請確認設定"));
					});

			// 新增下一步的 ApprovalStep
			ApprovalStep nextStep = new ApprovalStep();
			nextStep.setFlow(nextFlow);
			nextStep.setRequestId(requestId); // 設定為父類型 BaseRequest
			nextStep.setCurrentStep(nextFlow.getStepOrder());
			nextStep.setApprover(nextApprover);
			nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			nextStep.setUpdatedAt(LocalDateTime.now());

			approvalStepRepository.save(nextStep);
			
			//通知
			 String notifyMsg = "您有一筆新的補卡申請待審核：員工 " +request.getEmployee().getEmployeeName() + " 的《" + request.getClockType().getTypeName() + "》補卡申請。";
			 notifyService.sendNotification(nextApprover.getEmployeeId(), notifyMsg);
					
			
		} else {
			// 若沒有下一步，代表簽核完成，更新補卡單狀態
			System.out.println("更新申請單");
			Integer requestId = step.getRequestId();
			MissingPunchRequest request = missingPunchRequestRepository.findById(requestId)
					.orElseThrow(() -> new RuntimeException("找不到請假申請"));
			request.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			missingPunchRequestRepository.save(request);

			Employee employee = request.getEmployee();
			LocalDate missingDate = request.getMissingDate();

			ShiftType shiftType = scheduleRepository
					.findShiftTypeByEmployeeIdAndDate(employee.getEmployeeId(), missingDate)
					.orElseThrow(() -> new RuntimeException("找不到當日班別"));
			if (request.getClockType().getTypeName() == "上班") {
				System.out.println("上班");
				LocalDateTime startOfDay = missingDate.atStartOfDay();
				LocalDateTime endOfDay = startOfDay.plusDays(1);
				System.out.println(startOfDay);
				Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee,
						startOfDay, endOfDay);
				Attendance attendance = attendanceOpt.orElseThrow(() -> new RuntimeException("指定日期無考勤紀錄或員工不存在"));
				Optional<Type> clockInTypeOpt = typeRepository.findByTypeName("上班");
				AttendanceLogs log = new AttendanceLogs();
				log.setAttendance(attendance);
				log.setEmployee(employee);
				log.setClockType(clockInTypeOpt.orElseThrow(() -> new RuntimeException("上班打卡類型不存在")));
				log.setClockTime(missingDate.atTime(shiftType.getStartTime()));
				attendanceLogsRepository.save(log);
				Optional<AttendanceViolations> lateOpt = attendanceViolationsRepository.findByViolationType(
						typeRepository.findByTypeName("遲到").orElseThrow(() -> new RuntimeException("遲到違規類型不存在")));
				if (lateOpt.isPresent()) {
					attendanceViolationsRepository.deleteById(lateOpt.get().getId());
				}
				updateTotalHoursByClockCard(attendance, shiftType, clockInTypeOpt.get());
				
				// 通知成功
				String msg = "您的補卡申請《" + request.getClockType().getTypeName() + "》已通過審核。";
				notifyService.sendNotification(employee.getEmployeeId(), msg);
			}

			if (request.getClockType().getTypeName().equals("下班")) {
				LocalDateTime startOfDay = missingDate.atStartOfDay();
				LocalDateTime endOfDay = startOfDay.plusDays(1);

				Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee,
						startOfDay, endOfDay);
				Attendance attendance = attendanceOpt.orElseThrow(() -> new RuntimeException("指定日期無考勤紀錄或員工不存在"));
				Optional<Type> clockOutTypeOpt = typeRepository.findByTypeName("下班");
				AttendanceLogs log = new AttendanceLogs();
				log.setAttendance(attendance);
				log.setEmployee(employee);
				log.setClockType(clockOutTypeOpt.orElseThrow(() -> new RuntimeException("下班打卡類型不存在")));
				log.setClockTime(missingDate.atTime(shiftType.getStartTime()));
				attendanceLogsRepository.save(log);

				Optional<AttendanceViolations> earlyLeaveOpt = attendanceViolationsRepository.findByViolationType(
						typeRepository.findByTypeName("早退").orElseThrow(() -> new RuntimeException("早退違規類型不存在")));
				if (earlyLeaveOpt.isPresent()) {
					attendanceViolationsRepository.deleteById(earlyLeaveOpt.get().getId());
				}
				updateTotalHoursByClockCard(attendance, shiftType, clockOutTypeOpt.get());
				
				
				// 通知成功
				String finalSuccessMsg = "您的補卡申請《" + request.getClockType().getTypeName() + "》已通過審核。";
				notifyService.sendNotification(request.getEmployee().getEmployeeId(), finalSuccessMsg);
			}
		}

		return "簽核成功";
	}

	private void updateTotalHoursByClockCard(Attendance attendance, ShiftType shiftType, Type clockType) {
		// 取得所有打卡紀錄
		List<AttendanceLogs> attendanceLogs = attendanceLogsRepository.findByAttendance(attendance);

		// 找出最早的上班卡與最晚的下班卡
		Optional<AttendanceLogs> earliestClockIn = attendanceLogs.stream()
				.filter(log -> log.getClockType().getTypeName().equals("上班"))
				.min(Comparator.comparing(AttendanceLogs::getClockTime));

		Optional<AttendanceLogs> latestClockOut = attendanceLogs.stream()
				.filter(log -> log.getClockType().getTypeName().equals("下班"))
				.max(Comparator.comparing(AttendanceLogs::getClockTime));

		// 設定午休時間（分鐘）
		BigDecimal lunchBreakMinutes = BigDecimal.valueOf(60);

		// 計算總工時的通用方法
		BiFunction<LocalDateTime, LocalDateTime, BigDecimal> calculateWorkingHours = (start, end) -> {
			BigDecimal totalMinutes = BigDecimal.valueOf(Duration.between(start, end).toMinutes());
			return totalMinutes.subtract(lunchBreakMinutes).multiply(BigDecimal.valueOf(1.0 / 60)).setScale(2,
					RoundingMode.HALF_UP);
		};

		BigDecimal totalHours = BigDecimal.ZERO;

		// 若有上下班打卡紀錄，計算工時
		if (earliestClockIn.isPresent() && latestClockOut.isPresent()) {
			LocalDateTime clockInTime = earliestClockIn.get().getClockTime();
			LocalDateTime clockOutTime = latestClockOut.get().getClockTime();
			totalHours = calculateWorkingHours.apply(clockInTime, clockOutTime);
		}
		// 若只有下班打卡，則以班表的上班時間作為計算基準
		else if (clockType.getTypeName().equals("上班") && latestClockOut.isPresent()) {
			LocalDateTime clockOutTime = latestClockOut.get().getClockTime();
			LocalDateTime shiftStartTime = shiftType.getStartTime().atDate(clockOutTime.toLocalDate());
			totalHours = calculateWorkingHours.apply(shiftStartTime, clockOutTime);
		}
		// 若只有上班打卡，則以班表的下班時間作為計算基準
		else if (clockType.getTypeName().equals("下班") && earliestClockIn.isPresent()) {
			LocalDateTime clockInTime = earliestClockIn.get().getClockTime();
			LocalDateTime shiftEndTime = shiftType.getFinishTime().atDate(clockInTime.toLocalDate());
			totalHours = calculateWorkingHours.apply(clockInTime, shiftEndTime);
		}

		// 確保工時不為負數
		totalHours = totalHours.max(BigDecimal.ZERO);
		attendance.setTotalHours(totalHours);

		// 設定正規工時（若為 null 則預設為 8 小時）
		BigDecimal regularHours = attendance.getRegularHours() != null ? attendance.getRegularHours()
				: BigDecimal.valueOf(8);
		attendance.setRegularHours(regularHours);

		// 計算加班時間（總工時 - 正規工時）
		BigDecimal overtimeHours = totalHours.subtract(regularHours).max(BigDecimal.ZERO);
		attendance.setOvertimeHours(overtimeHours);

		// 更新是否有違規
		attendance.setHasViolation(attendanceViolationsRepository.existsByAttendance(attendance));

		// 保存更新的考勤
		attendanceRepository.save(attendance);
	}

	// 查詢員工待審核的費用單
	@Transactional
	public List<ExpenseApprovalStepDTO> getPendingExpenseApprovalSteps(Integer approverId) {
		// 查詢待審核的 ApprovalStep
		List<ApprovalStep> pendingSteps = approvalStepRepository.findPendingApprovalSteps(approverId, "待審核");

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return pendingSteps.stream()
				.filter(step -> "expense_type".equals(step.getFlow().getRequestType().getCategory())).map(step -> {
					Integer requestId = step.getRequestId();
					System.out.println("草" + requestId);
					// 避免 Optional 取值時發生錯誤
					ExpenseRequest request = expenseRequestRepository.findById(requestId)
							.orElseThrow(() -> new NoSuchElementException("找不到 ID 為 " + requestId + " 的加減班申請"));

					// 構建 ApprovalStepDTO
					return new ExpenseApprovalStepDTO(step.getId(), requestId, request.getEmployee().getEmployeeId(),
							request.getEmployee().getEmployeeName(), request.getExpenseType().getTypeName(),
							request.getAmount(), request.getDescription(), request.getSubmittedAt(),
							fileService.extractOriginalFileName(request.getAttachments()), request.getAttachments(),
							step.getApprover().getEmployeeId(), step.getApprover().getEmployeeName(),
							step.getStatus().getStatusName(), step.getCurrentStep(), step.getComment(),
							step.getUpdatedAt());
				}).collect(Collectors.toList());
	}

	// 啟動費用簽核
	@Transactional
	public void startExpenseApprovalProcess(ExpenseRequest expenseRequest) {

		// 取得員工的職位
		Integer positionId = expenseRequest.getEmployee().getPosition().getPositionId();
		Integer requestTypeId = expenseRequest.getExpenseType().getId();

		// 查詢對應的第一步驟簽核流程
		Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository.findApprovalFlow(positionId, requestTypeId, 1)
				.stream().findFirst();
		System.out.println(firstStepFlowOpt.get().getFlowName());
		// 先查詢員工專屬的簽核流程
		Optional<ApprovalFlow> employeeFlow = approvalFlowRepository
				.findFirstStepByEmployee(expenseRequest.getEmployee().getEmployeeId(), requestTypeId);

		if (employeeFlow.isPresent()) {
			firstStepFlowOpt = employeeFlow;
		}

		if (!firstStepFlowOpt.isPresent()) {
			throw new RuntimeException("未找到對應的簽核流程");
		}
		ApprovalFlow firstStepFlow = firstStepFlowOpt.get();
		
		
		// 找到該部門中符合該職位的第一位簽核人
		Optional<Employee> approverOpt = employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						expenseRequest.getEmployee().getDepartment(),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst();

		// 如果找不到符合條件的簽核人，進行二次查詢以處理特殊情況
		approverOpt = approverOpt.or(() -> employeeRepository
				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
						departmentRepository.findByDepartmentName("總經理部")
								.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
				.stream().findFirst());

		// 如果二次查詢仍然找不到簽核人，拋出異常
		Employee approver = approverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的簽核人，請確認設定"));
		
		
		// 打印簽核人的姓名（可用於調試）
		System.out.println(approver.getEmployeeName());

		// 創建簽核步驟
		ApprovalStep approvalStep = new ApprovalStep();
		approvalStep.setFlow(firstStepFlow);

		// 使用 BaseRequest 類型來設定請求
		approvalStep.setRequestId(expenseRequest.getId()); // leaveRequest 是 BaseRequest 類型的子類型

		approvalStep.setCurrentStep(1);
		approvalStep.setApprover(approver);
		approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態").orElse(null));
		approvalStep.setUpdatedAt(LocalDateTime.now());

		// 儲存簽核步驟
		approvalStepRepository.save(approvalStep);
		
		// 發送通知給第一位審核人
		String employeeName = expenseRequest.getEmployee().getEmployeeName();
		String typeName = expenseRequest.getExpenseType().getTypeName(); 
		String message = "您有一筆新的費用申請待審核：員工 " + employeeName + " 的《" + typeName + "》申請單。";
		notifyService.sendNotification(approver.getEmployeeId(), message);

	}

	// 費用簽核中
	@Transactional
	public String approveExpenseRequest(Integer approvalStepId, Integer approverUserId, String statusName,
			String comment) {
		ApprovalStep step = approvalStepRepository.findById(approvalStepId)
				.orElseThrow(() -> new RuntimeException("簽核步驟不存在"));

		// 檢查是否是正確的審核人
		if (!step.getApprover().getEmployeeId().equals(approverUserId)) {
			return "你沒有權限審核這個請假單";
		}

		// 更新當前審核步驟狀態
		step.setStatus(statusRepository.findByStatusNameAndStatusType(statusName, "表單狀態")
				.orElseThrow(() -> new RuntimeException("狀態不存在")));
		step.setComment(comment);
		step.setUpdatedAt(LocalDateTime.now());
		approvalStepRepository.save(step);

		// ** 如果否決，直接更新請假單狀態**
		if ("未核准".equals(step.getStatus().getStatusName())) {
			Integer requestId = step.getRequestId();
			Optional<ExpenseRequest> expenseRequestOpt = expenseRequestRepository.findById(requestId);
			ExpenseRequest request = expenseRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("未核准", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));

			expenseRequestRepository.save(request);
			
			
			// 通知申請人
			String failMsg = "您的費用申請《" + request.getExpenseType().getTypeName() + "》未通過審核。";
			notifyService.sendNotification(request.getEmployee().getEmployeeId(), failMsg);
			
			
			return "已否決請假單";
		}

		// ** 否則進入「核准」流程**
		ApprovalFlow currentFlow = step.getFlow();

		// 取得下一步流程
		ApprovalFlow nextStepFlow = currentFlow.getNextStep();
		if (nextStepFlow == null) {
			// 如果沒有下一步，直接完成簽核流程
			Integer requestId = step.getRequestId();
			Optional<ExpenseRequest> expenseRequestOpt = expenseRequestRepository.findById(requestId);
			ExpenseRequest request = expenseRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			expenseRequestRepository.save(request);
			
			// 通知申請人
			String successMsg = "您的費用申請《" + request.getExpenseType().getTypeName() + "》已通過審核。";
			notifyService.sendNotification(request.getEmployee().getEmployeeId(), successMsg);

			
			return "簽核成功";
		}

		// 繼續處理下一步流程
		Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findById(nextStepFlow.getId());
		if (nextFlowOpt.isPresent()) {
			// 若有下一步驟，新增下一個 `ApprovalStep`
			Integer requestId = step.getRequestId();
			Optional<ExpenseRequest> expenseRequestOpt = expenseRequestRepository.findById(requestId);
			ExpenseRequest request = expenseRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("審核中", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			expenseRequestRepository.save(request);

			// 獲取下一個審核人
			ApprovalFlow nextFlow = nextFlowOpt.get();
			Employee nextApprover = employeeRepository.findTopByPositionAndDepartmentAndStatus(
					nextFlow.getApproverPosition(), step.getApprover().getDepartment(),
					statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
					.orElseGet(() -> {
						Optional<Employee> fallbackApproverOpt = employeeRepository
								.findTopByPositionAndDepartmentAndStatus(nextFlow.getApproverPosition(),
										departmentRepository.findByDepartmentName("總經理部")
												.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
										statusRepository.findByStatusName("在職")
												.orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
								.stream().findFirst();

						return fallbackApproverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的審核人，請確認設定"));
					});

			// 新增下一步的 ApprovalStep
			ApprovalStep nextStep = new ApprovalStep();
			nextStep.setFlow(nextFlow);
			nextStep.setRequestId(requestId); // 設定為父類型 BaseRequest
			nextStep.setCurrentStep(nextFlow.getStepOrder());
			nextStep.setApprover(nextApprover);
			nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			nextStep.setUpdatedAt(LocalDateTime.now());

			approvalStepRepository.save(nextStep);
			
			// 通知下一位審核人
			String notifyMsg = "您有一筆新的費用申請待審核：員工 " + request.getEmployee().getEmployeeName() + " 的《"+ request.getExpenseType().getTypeName() + "》申請單。";
			notifyService.sendNotification(nextApprover.getEmployeeId(), notifyMsg);
			
			
		} else {
			// 若沒有下一步，代表簽核完成，更新費用單狀態
			Integer requestId = step.getRequestId();
			Optional<ExpenseRequest> expenseRequestOpt = expenseRequestRepository.findById(requestId);
			ExpenseRequest request = expenseRequestOpt.get();
			request.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
					.orElseThrow(() -> new RuntimeException("狀態不存在")));
			expenseRequestRepository.save(request);
			
			// 通知申請人
			String successMsg = "您的費用申請《" + request.getExpenseType().getTypeName() + "》已通過審核。";
			notifyService.sendNotification(request.getEmployee().getEmployeeId(), successMsg);
					
		}

		return "簽核成功";
	}


	
	// 查找全部簽核步驟1的簽核流程
	@Transactional
	public List<ApprovalFlowResponseDTO> getAllStepOneApprovalFlow() {
		List<ApprovalFlow> byFirstStepOrder = approvalFlowRepository.findByStepOrder(1);
		return byFirstStepOrder.stream()
				.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
						flow.getRequestType().getTypeName(), flow.getStepOrder(), flow.getPosition().getPositionName(),
						flow.getApproverPosition().getPositionName()))
				.collect(Collectors.toList());
	}

	// 查找全部簽核步驟1的簽核流程ByPage
	@Transactional
	public List<ApprovalFlowResponseDTO> getAllStepOneApprovalFlowByPage(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		List<ApprovalFlow> byFirstStepOrder = approvalFlowRepository.findByStepOrder(1);
		return byFirstStepOrder.stream()
				.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
						flow.getRequestType().getTypeName(), flow.getStepOrder(), flow.getPosition().getPositionName(),
						flow.getApproverPosition().getPositionName()))
				.collect(Collectors.toList());
	}

	public Page<ApprovalFlowResponseDTO> getFilteredApprovalFlows(int page, int size, String search, String position,
			String requestType) {
		Pageable pageable = PageRequest.of(page - 1, size);

		Page<ApprovalFlow> flows = approvalFlowRepository.findFilteredFlows(search, position, requestType, pageable);

		return flows.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
				flow.getRequestType().getTypeName(), flow.getStepOrder(), flow.getPosition().getPositionName(),
				flow.getApproverPosition().getPositionName()));
	}

	// 查找簽核流程及所有後續步驟
	@Transactional
	public List<ApprovalFlowResponseDTO> getApprovalFlowAndNextSteps(Integer flowId) {
		List<Integer> flowIdsToGet = new ArrayList<>();
		collectNextSteps(flowId, flowIdsToGet);
		List<ApprovalFlow> flowsToGet = approvalFlowRepository.findAllById(flowIdsToGet);
		return flowsToGet.stream()
				.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
						flow.getRequestType().getTypeName(), flow.getStepOrder(), flow.getPosition().getPositionName(),
						flow.getApproverPosition().getPositionName()))
				.collect(Collectors.toList());
	}

	// 查找全部的簽核流程
	@Transactional
	public List<ApprovalFlowResponseDTO> getAllApprovalFlow() {
		List<ApprovalFlow> byFirstStepOrder = approvalFlowRepository.findAll();
		return byFirstStepOrder.stream()
				.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
						flow.getRequestType().getTypeName(), flow.getStepOrder(), flow.getPosition().getPositionName(),
						flow.getApproverPosition().getPositionName()))
				.collect(Collectors.toList());
	}

	// 建立自訂簽核流程
	@Transactional
	public ResponseEntity<?> createApprovalFlow(List<ApprovalFlowDTO> flowSteps) {
		try {
			Integer previousStepId = null;
			for (ApprovalFlowDTO step : flowSteps) {
				ApprovalFlow newStep = new ApprovalFlow();
				newStep.setFlowName(step.getFlowName());
				newStep.setRequestType(typeRepository.findById(step.getRequestTypeId()).orElseThrow());
				newStep.setPosition(positionRepository.findById(step.getEmployeePositionId()).orElseThrow());
				newStep.setStepOrder(step.getStepOrder());
				newStep.setApproverPosition(positionRepository.findById(step.getApproverPositionId()).orElseThrow());
				approvalFlowRepository.save(newStep);

				// 更新前一步的 next_step_id
				if (previousStepId != null) {
					ApprovalFlow previousStep = approvalFlowRepository.findById(previousStepId).orElseThrow();
					previousStep.setNextStep(newStep);
					approvalFlowRepository.save(previousStep);
				}
				previousStepId = newStep.getId();
			}
			return ResponseEntity.ok("簽核流程建立成功");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("建立簽核流程失敗");
		}
	}

	// 刪除簽核流程及所有後續步驟
	@Transactional
	public ResponseEntity<?> deleteApprovalFlowAndNextSteps(Integer flowId) {
		List<Integer> flowIdsToDelete = new ArrayList<>();
		collectNextSteps(flowId, flowIdsToDelete);

		if (flowIdsToDelete.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到相關簽核流程");
		}

		// 確保這些步驟沒有正在使用的請假單
		boolean isInUse = approvalStepRepository.existsByFlowIdIn(flowIdsToDelete);
		if (isInUse) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("該流程已被請假單使用，無法刪除");
		}

		approvalFlowRepository.deleteAllById(flowIdsToDelete);
		return ResponseEntity.ok("簽核流程及所有後續步驟已刪除");
	}

	// 遞迴查找所有後續步驟
	@Transactional
	private void collectNextSteps(Integer flowId, List<Integer> flowIdsToDelete) {
		Optional<ApprovalFlow> stepOpt = approvalFlowRepository.findById(flowId);
		if (stepOpt.isPresent()) {
			ApprovalFlow step = stepOpt.get();
			flowIdsToDelete.add(step.getId());
			if (step.getNextStep() != null) {
				collectNextSteps(step.getNextStep().getId(), flowIdsToDelete);
			}
		}
	}
}
