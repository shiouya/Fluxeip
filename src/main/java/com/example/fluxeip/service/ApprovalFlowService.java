package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.ApprovalFlowDTO;
import com.example.fluxeip.dto.ApprovalFlowResponseDTO;
import com.example.fluxeip.dto.ApprovalStepDTO;
import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.BaseRequest;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.model.Request;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.repository.ApprovalFlowRepository;
import com.example.fluxeip.repository.ApprovalStepRepository;
import com.example.fluxeip.repository.DepartmentRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.LeaveRequestRepository;
import com.example.fluxeip.repository.PositionRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;

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
	private FileService fileService;

	// 查詢員工待審核的請假單
	public List<ApprovalStepDTO> getPendingApprovalSteps(Integer approverId) {
	    // 查詢待審核的 ApprovalStep
	    List<ApprovalStep> pendingSteps = approvalStepRepository.findPendingApprovalSteps(approverId, "待審核");

	    // 將 ApprovalStep 轉換為 ApprovalStepDTO
	    return pendingSteps.stream()
	            .map(step -> {
	                BaseRequest baseRequest = step.getBaseRequest();
	                LeaveRequest leaveRequest = null;

	                // 確保 baseRequest 是 LeaveRequest 類型，然後強制轉型
	                if (baseRequest instanceof LeaveRequest) {
	                    leaveRequest = (LeaveRequest) baseRequest;  // 強制轉型為 LeaveRequest
	                }

	                // 構建 ApprovalStepDTO
	                return new ApprovalStepDTO(
	                        step.getId(),
	                        baseRequest.getId(),
	                        baseRequest.getEmployee().getEmployeeId(),
	                        baseRequest.getEmployee().getEmployeeName(),
	                        leaveRequest != null ? leaveRequest.getLeaveType().getTypeName() : null,  // 只有 LeaveRequest 才有 leaveType
	                        leaveRequest != null ? leaveRequest.getStartDatetime() : null,
	                        leaveRequest != null ? leaveRequest.getEndDatetime() : null,
	                        leaveRequest != null ? leaveRequest.getLeaveHours() : null,
	                        leaveRequest != null ? leaveRequest.getReason() : null,
	                        leaveRequest != null ? leaveRequest.getSubmittedAt() : null,
	                        fileService.extractOriginalFileName(leaveRequest != null ? leaveRequest.getAttachments() : null),
	                        leaveRequest != null ? leaveRequest.getAttachments() : null,
	                        step.getApprover().getEmployeeId(),
	                        step.getApprover().getEmployeeName(),
	                        step.getStatus().getStatusName(),
	                        step.getCurrentStep(),
	                        step.getComment(),
	                        step.getUpdatedAt()
	                );
	            })
	            .collect(Collectors.toList());
	}


	public void startLeaveApprovalProcess(LeaveRequest leaveRequest) {
		// 取得員工的職位
		Integer positionId = leaveRequest.getEmployee().getPosition().getPositionId();
		Integer requestTypeId = leaveRequest.getLeaveType().getId();
		
	    // 查詢對應的第一步驟簽核流程
	 	Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository.findApprovalFlow(positionId, requestTypeId, 1).stream().findFirst();
	 	System.out.println(firstStepFlowOpt.get().getFlowName());
	 	// 先查詢員工專屬的簽核流程
	 	Optional<ApprovalFlow> employeeFlow = approvalFlowRepository.findFirstStepByEmployee(leaveRequest.getEmployee().getEmployeeId(), requestTypeId);
	 	
	    if (employeeFlow.isPresent()) {
	    	firstStepFlowOpt=employeeFlow;
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

		// 使用 BaseRequest 類型來設定請求
		approvalStep.setBaseRequest(leaveRequest);  // leaveRequest 是 BaseRequest 類型的子類型

		approvalStep.setCurrentStep(1);
		approvalStep.setApprover(approver);
		approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態").orElse(null));
		approvalStep.setUpdatedAt(LocalDateTime.now());

		// 儲存簽核步驟
		approvalStepRepository.save(approvalStep); 

	}

	public String approveLeaveRequest(Integer approvalStepId, Integer approverUserId, String statusName, String comment) {
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
	    	BaseRequest baseRequest = step.getBaseRequest(); // 獲取 BaseRequest
	        if (baseRequest instanceof LeaveRequest) { // 檢查是否是 LeaveRequest
	            LeaveRequest leaveRequest = (LeaveRequest) baseRequest; // 強制轉型為 LeaveRequest
	            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("未核准", "表單狀態")
	                    .orElseThrow(() -> new RuntimeException("狀態不存在")));

	            leaveRequestRepository.save(leaveRequest);
	            return "已否決請假單";
	        } else {
	            throw new RuntimeException("請求類型不正確，無法處理");
	        }
	    }

	    // ** 否則進入「核准」流程**
	    ApprovalFlow currentFlow = step.getFlow();
	    
	    // 取得下一步流程
	    ApprovalFlow nextStepFlow = currentFlow.getNextStep();
	    if (nextStepFlow == null) {
	        // 如果沒有下一步，直接完成簽核流程
	    	BaseRequest baseRequest = step.getBaseRequest(); // 獲取 BaseRequest
	        if (baseRequest instanceof LeaveRequest) {
	            LeaveRequest leaveRequest = (LeaveRequest) baseRequest; // 強制轉型為 LeaveRequest
	            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
	                    .orElseThrow(() -> new RuntimeException("狀態不存在")));
	            leaveRequestRepository.save(leaveRequest);
	            return "簽核成功";
	        } else {
	            throw new RuntimeException("請求類型不正確，無法處理");
	        }
	    }

	    // 繼續處理下一步流程
	    Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findById(
	            nextStepFlow.getId());
	    if (nextFlowOpt.isPresent()) {
	        // 若有下一步驟，新增下一個 `ApprovalStep`
	    	BaseRequest baseRequest = step.getBaseRequest();
	        if (baseRequest instanceof LeaveRequest) {
	            LeaveRequest leaveRequest = (LeaveRequest) baseRequest; // 強制轉型為 LeaveRequest
	            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("審核中", "表單狀態")
	                    .orElseThrow(() -> new RuntimeException("狀態不存在")));
	            leaveRequestRepository.save(leaveRequest);
	        } else {
	            throw new RuntimeException("請求類型不正確，無法處理");
	        }

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
	        nextStep.setBaseRequest(baseRequest); // 設定為父類型 BaseRequest
	        nextStep.setCurrentStep(nextFlow.getStepOrder());
	        nextStep.setApprover(nextApprover);
	        nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態")
	                .orElseThrow(() -> new RuntimeException("狀態不存在")));
	        nextStep.setUpdatedAt(LocalDateTime.now());

	        approvalStepRepository.save(nextStep);
	    } else {
	        // 若沒有下一步，代表簽核完成，更新請假單狀態
	    	BaseRequest baseRequest = step.getBaseRequest();
	        if (baseRequest instanceof LeaveRequest) {
	            LeaveRequest leaveRequest = (LeaveRequest) baseRequest; // 強制轉型為 LeaveRequest
	            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
	                    .orElseThrow(() -> new RuntimeException("狀態不存在")));
	            leaveRequestRepository.save(leaveRequest);
	        } else {
	            throw new RuntimeException("請求類型不正確，無法處理");
	        }
	    }

	    return "簽核成功";
	}

	
	
	
	
	
	
//	public void startWorkAdjustApprovalProcess(WorkAdjustmentRequest workAdjustmentRequest) {
//		// 取得員工的職位
//		Integer positionId = workAdjustmentRequest.getEmployee().getPosition().getPositionId();
//		Integer requestTypeId = workAdjustmentRequest.getAdjustmentType().getId();
//		
//	    // 查詢對應的第一步驟簽核流程
//	 	Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository.findApprovalFlow(positionId, requestTypeId, 1).stream().findFirst();
//	 	System.out.println(firstStepFlowOpt.get().getFlowName());
//	 	// 先查詢員工專屬的簽核流程
//	 	Optional<ApprovalFlow> employeeFlow = approvalFlowRepository.findFirstStepByEmployee(workAdjustmentRequest.getEmployee().getEmployeeId(), requestTypeId);
//	 	
//	    if (employeeFlow.isPresent()) {
//	    	firstStepFlowOpt=employeeFlow;
//	    }	    
//		
//
//		if (!firstStepFlowOpt.isPresent()) {
//			throw new RuntimeException("未找到對應的簽核流程");
//		}
//		ApprovalFlow firstStepFlow = firstStepFlowOpt.get();
//  
//		// 找到該部門中符合該職位的第一位簽核人
//		Optional<Employee> approverOpt = employeeRepository
//				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
//						workAdjustmentRequest.getEmployee().getDepartment(),
//						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
//				.stream().findFirst();
//
//		// 如果找不到符合條件的簽核人，進行二次查詢以處理特殊情況
//		approverOpt = approverOpt.or(() -> employeeRepository
//				.findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(),
//						departmentRepository.findByDepartmentName("總經理部")
//								.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
//						statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
//				.stream().findFirst());
//
//		// 如果二次查詢仍然找不到簽核人，拋出異常
//		Employee approver = approverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的簽核人，請確認設定"));
//
//		// 打印簽核人的姓名（可用於調試）
//		System.out.println(approver.getEmployeeName());
//
//
//		// 創建簽核步驟
//		ApprovalStep approvalStep = new ApprovalStep();
//		approvalStep.setFlow(firstStepFlow);
//		approvalStep.setLeaveRequest(workAdjustmentRequest);
//		approvalStep.setCurrentStep(1);
//		approvalStep.setApprover(approver);
//		approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態").orElse(null));
//		approvalStep.setUpdatedAt(LocalDateTime.now());
//
//		// 儲存簽核步驟
//		approvalStepRepository.save(approvalStep);
//	}
//
//	public String approveLeaveRequest(Integer approvalStepId, Integer approverUserId, String statusName,
//			String comment) {
//		ApprovalStep step = approvalStepRepository.findById(approvalStepId)
//				.orElseThrow(() -> new RuntimeException("簽核步驟不存在"));
//		// 檢查是否是正確的審核人
//		if (!step.getApprover().getEmployeeId().equals(approverUserId)) {
//			return "你沒有權限審核這個請假單";
//		}
//
//		// 更新當前審核步驟狀態
//		step.setStatus(statusRepository.findByStatusNameAndStatusType(statusName, "表單狀態")
//				.orElseThrow(() -> new RuntimeException("狀態不存在")));
//		step.setComment(comment);
//		step.setUpdatedAt(LocalDateTime.now());
//		approvalStepRepository.save(step);
//
//		// ** 如果否決，直接更新請假單狀態**
//		if ("未核准".equals(step.getStatus().getStatusName())) {
//			LeaveRequest leaveRequest = step.getLeaveRequest();
//			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("未核准", "表單狀態")
//					.orElseThrow(() -> new RuntimeException("狀態不存在")));
//
//			leaveRequestRepository.save(leaveRequest);
//			return "已否決請假單";
//		}
//
//		// ** 否則進入「核准」流程**
//		ApprovalFlow currentFlow = step.getFlow();
//		
////		Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findApprovalFlow(
////				currentFlow.getPosition().getPositionId(), currentFlow.getRequestType().getId(),
////				currentFlow.getStepOrder() + 1);
//		ApprovalFlow nextStepFlow = currentFlow.getNextStep();
//		if (nextStepFlow == null) {
//	        // 如果沒有下一步，直接完成簽核流程
//	        LeaveRequest leaveRequest = step.getLeaveRequest();
//	        leaveRequest.setStatus(
//	            statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
//	                .orElseThrow(() -> new RuntimeException("狀態不存在"))
//	        );
//	        leaveRequestRepository.save(leaveRequest);
//	        return "簽核成功";
//	    }
//
//		Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findById(
//				currentFlow.getNextStep().getId());
//		if (nextFlowOpt.isPresent()) {
//			// 若有下一步驟，新增下一個 `ApprovalStep`
//			LeaveRequest leaveRequest = step.getLeaveRequest();
//			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("審核中", "表單狀態")
//					.orElseThrow(() -> new RuntimeException("狀態不存在")));
//			leaveRequestRepository.save(leaveRequest);
//			ApprovalFlow nextFlow = nextFlowOpt.get();
//			Employee nextApprover = employeeRepository.findTopByPositionAndDepartmentAndStatus(
//					nextFlow.getApproverPosition(), step.getApprover().getDepartment(),
//					statusRepository.findByStatusName("在職").orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
//					.orElseGet(() -> {
//						Optional<Employee> fallbackApproverOpt = employeeRepository
//								.findTopByPositionAndDepartmentAndStatus(nextFlow.getApproverPosition(),
//										departmentRepository.findByDepartmentName("總經理部")
//												.orElseThrow(() -> new RuntimeException("部門名稱有誤，請確認設定")),
//										statusRepository.findByStatusName("在職")
//												.orElseThrow(() -> new RuntimeException("狀態名稱有誤，請確認設定")))
//								.stream().findFirst();
//
//						return fallbackApproverOpt.orElseThrow(() -> new RuntimeException("找不到該部門的審核人，請確認設定"));
//					});
//
//			ApprovalStep nextStep = new ApprovalStep();
//			nextStep.setFlow(nextFlow);
//			nextStep.setLeaveRequest(step.getLeaveRequest());
//			nextStep.setCurrentStep(nextFlow.getStepOrder());
//			nextStep.setApprover(nextApprover);
//			nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核", "表單狀態")
//					.orElseThrow(() -> new RuntimeException("狀態不存在")));
//			nextStep.setUpdatedAt(LocalDateTime.now());
//
//			approvalStepRepository.save(nextStep);
//		} else {
//			// 若沒有下一步，代表簽核完成，更新請假單狀態
//			LeaveRequest leaveRequest = step.getLeaveRequest();
//			leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("已核決", "表單狀態")
//					.orElseThrow(() -> new RuntimeException("狀態不存在")));
//			leaveRequestRepository.save(leaveRequest);
//		}
//
//		return "簽核成功";
//	}
//
//	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//查找全部簽核步驟1的簽核流程
	public List<ApprovalFlowResponseDTO> getAllStepOneApprovalFlow(){
		List<ApprovalFlow> byFirstStepOrder = approvalFlowRepository.findByStepOrder(1);
		return byFirstStepOrder.stream()
				.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
						flow.getRequestType().getTypeName(),
						flow.getStepOrder(),
						flow.getPosition().getPositionName(),
						flow.getApproverPosition().getPositionName()))
				.collect(Collectors.toList());
	}
	
	//查找全部的簽核流程
	public List<ApprovalFlowResponseDTO> getAllApprovalFlow(){
		List<ApprovalFlow> byFirstStepOrder = approvalFlowRepository.findAll();
		return byFirstStepOrder.stream() 
				.map(flow -> new ApprovalFlowResponseDTO(flow.getId(), flow.getFlowName(),
						flow.getRequestType().getTypeName(),
						flow.getStepOrder(),
						flow.getPosition().getPositionName(),
						flow.getApproverPosition().getPositionName()))
				.collect(Collectors.toList());
	}
	
	
	
	
    // 建立自訂簽核流程
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
