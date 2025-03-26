package com.example.fluxeip.service;

import com.example.fluxeip.dto.ApprovalStepResponseDTO;
import com.example.fluxeip.model.*;
import com.example.fluxeip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApprovalService {
	@Autowired
	private ApprovalFlowRepository approvalFlowRepository;

	@Autowired
	private ApprovalStepRepository approvalStepRepository;

	@Autowired
	private EmployeeApprovalFlowRepository employeeApprovalFlowRepository;

	@Autowired
	private LeaveRequestRepository leaveRequestRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private WorkAdjustmentRequestRepository adjustmentRequestRepository;

	@Autowired
	private MissingPunchRequestRepository missingPunchRequestRepository;

	@Autowired
	private ExpenseRequestRepository expenseRequestRepository;

	// 取得某類請求的簽核流程
	public List<ApprovalFlow> getApprovalFlowForType(Integer requestTypeId) {
		return approvalFlowRepository.findByRequestTypeId(requestTypeId);
	}

//    // 建立新的簽核步驟
//    public ApprovalStep createApprovalStep(Integer requestId, Integer flowId, Employee approver) {
//        ApprovalStep step = new ApprovalStep();
//        step.setLeaveRequest(leaveRequestRepository.findById(requestId).get());
//        step.setFlow(approvalFlowRepository.findById(flowId).orElseThrow());
//        step.setApprover(approver);
//        step.setStatus(new Status(1, "Pending","all_approval"));
//        return approvalStepRepository.save(step);
//    }

	// 查詢員工的請假單審核步驟
	public List<ApprovalStepResponseDTO> getLeaveApprovalStepsByRequestId(Integer requestId) {
		// 查詢正在審核的 ApprovalStep
		List<ApprovalStep> steps = approvalStepRepository.findByRequestIdOrderByCurrentStepAsc(requestId);

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return steps.stream().map(step -> {
			Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(requestId);
			LeaveRequest leaveRequest = leaveRequestOpt.get();
			// 如果 BaseRequest 實際上是 LeaveRequest，進行類型轉換

			return new ApprovalStepResponseDTO(step.getId(), leaveRequest.getId(), // 如果是 LeaveRequest，取其 ID
					leaveRequest.getEmployee().getEmployeeId(), // 同上
					leaveRequest.getEmployee().getEmployeeName(), // 同上
					step.getApprover().getEmployeeId(), step.getApprover().getEmployeeName(),
					step.getStatus().getStatusName(), step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
		}).collect(Collectors.toList());
	}

	// 查詢員工的加減班單審核步驟
	public List<ApprovalStepResponseDTO> getWorkadjustApprovalStepsByRequestId(Integer requestId) {
		// 查詢正在審核的 ApprovalStep
		List<ApprovalStep> steps = approvalStepRepository.findByRequestIdOrderByCurrentStepAsc(requestId);

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return steps.stream().map(step -> {
			Optional<WorkAdjustmentRequest> workAdjustRequestOpt = adjustmentRequestRepository.findById(requestId);
			WorkAdjustmentRequest request = workAdjustRequestOpt.get();

			return new ApprovalStepResponseDTO(step.getId(), request.getId(), // 如果是 LeaveRequest，取其 ID
					request.getEmployee().getEmployeeId(), // 同上
					request.getEmployee().getEmployeeName(), // 同上
					step.getApprover().getEmployeeId(), step.getApprover().getEmployeeName(),
					step.getStatus().getStatusName(), step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
		}).collect(Collectors.toList());
	}

	// 查詢員工的補卡單審核步驟
	public List<ApprovalStepResponseDTO> getMissingPunchApprovalStepsByRequestId(Integer requestId) {
		// 查詢正在審核的 ApprovalStep
		List<ApprovalStep> steps = approvalStepRepository.findByRequestIdOrderByCurrentStepAsc(requestId);

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return steps.stream().map(step -> {
			Optional<MissingPunchRequest> missingPunchRequestOpt = missingPunchRequestRepository.findById(requestId);
			MissingPunchRequest request = missingPunchRequestOpt.get();

			return new ApprovalStepResponseDTO(step.getId(), request.getId(), // ，取其 ID
					request.getEmployee().getEmployeeId(), // 同上
					request.getEmployee().getEmployeeName(), // 同上
					step.getApprover().getEmployeeId(), step.getApprover().getEmployeeName(),
					step.getStatus().getStatusName(), step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
		}).collect(Collectors.toList());
	}

	// 查詢員工的費用單審核步驟
	public List<ApprovalStepResponseDTO> getExpenseApprovalStepsByRequestId(Integer requestId) {
		// 查詢正在審核的 ApprovalStep
		List<ApprovalStep> steps = approvalStepRepository.findByRequestIdOrderByCurrentStepAsc(requestId);

		// 將 ApprovalStep 轉換為 ApprovalStepDTO
		return steps.stream().map(step -> {
			Optional<ExpenseRequest> expenseRequestOpt = expenseRequestRepository.findById(requestId);
			ExpenseRequest request = expenseRequestOpt.get();

			return new ApprovalStepResponseDTO(step.getId(), request.getId(), // ，取其 ID
					request.getEmployee().getEmployeeId(), // 同上
					request.getEmployee().getEmployeeName(), // 同上
					step.getApprover().getEmployeeId(), step.getApprover().getEmployeeName(),
					step.getStatus().getStatusName(), step.getCurrentStep(), step.getComment(), step.getUpdatedAt());
		}).collect(Collectors.toList());
	}

//    // 更新簽核狀態
//    public ApprovalStep updateApprovalStep(Integer stepId, Integer statusId, String comment) {
//        Optional<ApprovalStep> optionalStep = approvalStepRepository.findById(stepId);
//        if (optionalStep.isPresent()) {
//            ApprovalStep step = optionalStep.get();
//            step.setStatus(new Status(statusId, statusId == 2 ? "Approved" : "Rejected","all_approval"));
//            step.setComment(comment);
//            return approvalStepRepository.save(step);
//        } 
//        return null;  
//    }

//    public boolean approveStep(Integer stepId, String comment) {
//        Optional<ApprovalStep> optionalStep = approvalStepRepository.findById(stepId);
//        if (optionalStep.isPresent()) {
//            ApprovalStep step = optionalStep.get();
//            step.setStatus("已核准");
//            step.setComment(comment);
//            approvalStepRepository.save(step);
//
//            // 從 ApprovalFlow 中取得下一個步驟
//            ApprovalFlow nextFlowStep = approvalFlowRepository.findNextFlowStep(step.getFlow().getNextStep().getId());
//            if (nextFlowStep != null) {
//                // 建立新的 ApprovalStep
//                ApprovalStep nextStep = new ApprovalStep();
//                nextStep.setLeaveRequest(step.getLeaveRequest());
//                nextStep.setFlow(step.getFlow());
//                nextStep.setCurrentStep(nextFlowStep.getStepOrder());
//                nextStep.setApprover(nextFlowStep.getApproverPosition()); // 指定下一個審核人
//                nextStep.setStatus("待審核");
//                approvalStepRepository.save(nextStep);
//            } else {
//                // 如果沒有下一步，則更新請假表單狀態為「已核決」
//                LeaveRequest leaveRequest = leaveRequestRepository.findById(step.getRequestId()).orElse(null);
//                if (leaveRequest != null) {
//                    leaveRequest.setStatus("已核決");
//                    leaveRequestRepository.save(leaveRequest);
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//
//
//    public boolean rejectStep(Integer stepId, String comment) {
//        Optional<ApprovalStep> optionalStep = approvalStepRepository.findById(stepId);
//        if (optionalStep.isPresent()) {
//            ApprovalStep step = optionalStep.get();
//            step.setStatus(statusRepository.findByStatusName("未核准").get());
//            step.setComment(comment);
//            approvalStepRepository.save(step);
//
//            // 更新請假表單狀態為「未核准」
//            LeaveRequest leaveRequest = leaveRequestRepository.findById(step.getLeaveRequest().getId()).orElse(null);
//            if (leaveRequest != null) {
//                leaveRequest.setStatus(statusRepository.findByStatusName("未核准").get());
//                leaveRequestRepository.save(leaveRequest);
//            }
//            return true;
//        }
//        return false;
//    }
}
