package com.example.fluxeip.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.ApprovalFlowResponseDTO;
import com.example.fluxeip.dto.EmployeeApprovalFlowDTO;
import com.example.fluxeip.dto.EmployeeApprovalFlowResponseDTO;
import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeApprovalFlow;
import com.example.fluxeip.repository.ApprovalFlowRepository;
import com.example.fluxeip.repository.ApprovalStepRepository;
import com.example.fluxeip.repository.EmployeeApprovalFlowRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.ExpenseRequestRepository;
import com.example.fluxeip.repository.LeaveRequestRepository;
import com.example.fluxeip.repository.MissingPunchRequestRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;
import com.example.fluxeip.repository.WorkAdjustmentRequestRepository;

@Service
public class EmployeeApprovalFlowService {
	@Autowired
	private EmployeeApprovalFlowRepository employeeApprovalFlowRepository;

	@Autowired
	private ApprovalFlowRepository approvalFlowRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ApprovalStepRepository approvalStepRepository;

	@Autowired
	private LeaveRequestRepository leaveRequestRepository;

	@Autowired
	private WorkAdjustmentRequestRepository workAdjustmentRequestRepository;

	@Autowired
	private MissingPunchRequestRepository missingPunchRequestRepository;

	@Autowired
	private ExpenseRequestRepository expenseRequestRepository;

	// 建立員工與自訂流程的關係
	public void createEmployeeApprovalFlows(List<EmployeeApprovalFlowDTO> dtos) {
		List<EmployeeApprovalFlow> approvalFlows = new ArrayList<>();

		for (EmployeeApprovalFlowDTO dto : dtos) {
			// 驗證員工、簽核流程是否存在
			Employee employee = employeeRepository.findById(dto.getEmployeeId())
					.orElseThrow(() -> new RuntimeException("員工不存在: " + dto.getEmployeeId()));

			ApprovalFlow approvalFlow = approvalFlowRepository.findById(dto.getFlowId())
					.orElseThrow(() -> new RuntimeException("簽核流程不存在: " + dto.getFlowId()));

			// **檢查是否已存在綁定**
			boolean exists = employeeApprovalFlowRepository.existsByEmployeeAndApprovalFlow(employee, approvalFlow);
			if (exists) {
				throw new RuntimeException("某員工已綁定選定之簽核流程: " + dto.getFlowId());
			}
			// 創建自訂簽核流程
			EmployeeApprovalFlow employeeApprovalFlow = new EmployeeApprovalFlow();
			employeeApprovalFlow.setEmployee(employee);
			employeeApprovalFlow.setType(approvalFlow.getRequestType());
			employeeApprovalFlow.setApprovalFlow(approvalFlow);

			approvalFlows.add(employeeApprovalFlow);
		}

		// 批量儲存
		employeeApprovalFlowRepository.saveAll(approvalFlows);
	}

	// 查詢員工與自訂流程的關係
	public List<EmployeeApprovalFlowResponseDTO> getEmployeeApprovalFlowsByEmployeeId(Integer employeeId) {

		List<EmployeeApprovalFlow> listbyEmployeeId = employeeApprovalFlowRepository.findByEmployeeId(employeeId);

		// 將 EmployeeApprovalFlow 轉換為 ApprovalFlowResponseDTO
		return listbyEmployeeId.stream().map(employeeFlow -> {
			Optional<ApprovalFlow> optional = approvalFlowRepository.findById(employeeFlow.getApprovalFlow().getId());
			ApprovalFlow approvalFlow = optional.get();

			return new EmployeeApprovalFlowResponseDTO(employeeFlow.getId(), employeeFlow.getApprovalFlow().getId(),
					approvalFlow.getFlowName(), approvalFlow.getRequestType().getTypeName(),
					approvalFlow.getStepOrder(), approvalFlow.getPosition().getPositionName(),
					approvalFlow.getApproverPosition().getPositionName());
		}).collect(Collectors.toList());
	}

	// 解除員工與流程綁定
	@Transactional
	public String deleteEmployeeApprovalFlowsById(Integer id) {
		EmployeeApprovalFlow employeeApprovalFlow = employeeApprovalFlowRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("未找到對應的員工簽核流程"));

		Integer employeeId = employeeApprovalFlow.getEmployee().getEmployeeId(); // 取得員工ID

		List<Integer> flowIdsToDelete = new ArrayList<>();
		collectNextSteps(employeeApprovalFlow.getApprovalFlow().getId(), flowIdsToDelete);

		if (flowIdsToDelete.isEmpty()) {
			throw new RuntimeException("未找到相關簽核流程");
		}

		// 取該流程的 requestTypeCategory
		String requestTypeCategory = employeeApprovalFlow.getApprovalFlow().getRequestType().getCategory();

		// 取所有對應流程的 step，並從中找出 requestId
		List<ApprovalStep> steps = approvalStepRepository.findByFlowIdIn(flowIdsToDelete);
		List<Integer> requestIds = steps.stream().map(ApprovalStep::getRequestId).collect(Collectors.toList());

		// 確保這些 requestId 不屬於「已核決」的申請，且屬於該員工
		boolean isInUse = isRequestInPendingState(requestTypeCategory, requestIds, employeeId);
		if (isInUse) {
			throw new RuntimeException("該流程有未核決的申請單，無法刪除");
		}

		// 若無影響則刪除
		employeeApprovalFlowRepository.deleteById(id);
		return "流程已與該員工解綁";
	}

	@Transactional
	public void deleteAllEmployeeApprovalFlowsByEmployeeId(Integer employeeId) {
	    // 取得該員工所有的簽核流程
	    List<EmployeeApprovalFlow> flowsbyEmployeeId = employeeApprovalFlowRepository.findByEmployeeId(employeeId);

	    if (flowsbyEmployeeId.isEmpty()) {
	        System.out.println("該員工無對應的簽核流程");
	        return; // 記錄錯誤但不拋出異常
	    }

	    for (EmployeeApprovalFlow employeeApprovalFlow : flowsbyEmployeeId) {
	        Integer flowId = employeeApprovalFlow.getApprovalFlow().getId();
	        Integer empId = employeeApprovalFlow.getEmployee().getEmployeeId(); // 取得員工ID

	        List<Integer> flowIdsToDelete = new ArrayList<>();
	        collectNextSteps(flowId, flowIdsToDelete);

	        if (flowIdsToDelete.isEmpty()) {
	            continue; // 記錄錯誤但不拋出異常，繼續處理下一個
	        }

	        // 取該流程的 requestTypeCategory
	        String requestTypeCategory = employeeApprovalFlow.getApprovalFlow().getRequestType().getCategory();

	        // 取所有對應流程的 step，並從中找出 requestId
	        List<ApprovalStep> steps = approvalStepRepository.findByFlowIdIn(flowIdsToDelete);
	        List<Integer> requestIds = steps.stream().map(ApprovalStep::getRequestId).collect(Collectors.toList());

	        // 確保這些 requestId 不屬於「已核決」的申請，且屬於該員工
	        boolean isInUse = isRequestInPendingState(requestTypeCategory, requestIds, empId);
	        if (isInUse) {
	            throw new RuntimeException("該流程有未核決的申請單，無法刪除");
	        }

	        // 若無影響則刪除
	        employeeApprovalFlowRepository.delete(employeeApprovalFlow);
	    }
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

	// 檢查 requestId 是否未核決且屬於該員工
	private boolean isRequestInPendingState(String requestType, List<Integer> requestIds, Integer employeeId) {
		if (requestIds.isEmpty()) {
			return false; // 沒有任何 requestId，代表流程未被使用
		}

		// 取 "已核決" 的 statusId
		Integer approvedStatusId = statusRepository.findByStatusName("已核決")
				.orElseThrow(() -> new RuntimeException("無法找到 '已核決' 的狀態")).getStatusId();

		// 取 "未核准" 的 statusId
		Integer rejectedStatusId = statusRepository.findByStatusName("未核准")
				.orElseThrow(() -> new RuntimeException("無法找到 '未核准' 的狀態")).getStatusId();

		switch (requestType) {
		case "leave_type":
			return leaveRequestRepository.existsByRequestIdsAndStatusNotInAndEmployeeId(requestIds,
					Arrays.asList(approvedStatusId, rejectedStatusId), employeeId);
		case "work_adjustment_type":
			return workAdjustmentRequestRepository.existsByRequestIdsAndStatusNotInAndEmployeeId(requestIds,
					Arrays.asList(approvedStatusId, rejectedStatusId), employeeId);
		case "expense_type":
			return expenseRequestRepository.existsByRequestIdsAndStatusNotInAndEmployeeId(requestIds,
					Arrays.asList(approvedStatusId, rejectedStatusId), employeeId);
		case "clock_type":
			return missingPunchRequestRepository.existsByRequestIdsAndStatusNotInAndEmployeeId(requestIds,
					Arrays.asList(approvedStatusId, rejectedStatusId), employeeId);
		default:
			return false; // 無效類型則不影響刪除
		}
	}

}
