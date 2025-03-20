package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.ApprovalStepDTO;
import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.repository.ApprovalFlowRepository;
import com.example.fluxeip.repository.ApprovalStepRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.LeaveRequestRepository;
import com.example.fluxeip.repository.StatusRepository;

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
    private FileService fileService;
    

    // 查詢員工待審核的請假單
    public List<ApprovalStepDTO> getPendingApprovalSteps(Integer approverId) {
        // 查詢待審核的 ApprovalStep
        List<ApprovalStep> pendingSteps = approvalStepRepository.findPendingApprovalSteps(approverId, "待審核");

        // 將 ApprovalStep 轉換為 ApprovalStepDTO
        return pendingSteps.stream().map(step -> new ApprovalStepDTO(
        		step.getLeaveRequest().getId(),
                step.getLeaveRequest().getEmployee().getEmployeeId(),
                step.getLeaveRequest().getEmployee().getEmployeeName(),
                step.getLeaveRequest().getLeaveType().getTypeName(),
                step.getLeaveRequest().getStartDatetime(),
                step.getLeaveRequest().getEndDatetime(),
                step.getLeaveRequest().getLeaveHours(),
                step.getLeaveRequest().getReason(),
                step.getLeaveRequest().getSubmittedAt(),
                fileService.extractOriginalFileName(step.getLeaveRequest().getAttachments()),
                step.getLeaveRequest().getAttachments(),
                step.getApprover().getEmployeeId(),
                step.getApprover().getEmployeeName(),
                step.getStatus().getStatusName(),
                step.getCurrentStep(),
                step.getComment(),
                step.getUpdatedAt()
        )).collect(Collectors.toList());
    }

    public void startApprovalProcess(LeaveRequest leaveRequest) {
    	System.out.println("hahaha");
        // 取得員工的職位
        Integer positionId = leaveRequest.getEmployee().getPosition().getPositionId();
        System.out.println("positionId"+positionId);
        Integer requestTypeId = leaveRequest.getLeaveType().getId();
        System.out.println("requestTypeId"+requestTypeId);
        // 查詢對應的第一步驟簽核流程
        Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository
            .findApprovalFlow(positionId, requestTypeId, 1);
        System.out.println(firstStepFlowOpt.get().getFlowName());
        
        if (!firstStepFlowOpt.isPresent()) {
            throw new RuntimeException("未找到對應的簽核流程");
        }
        ApprovalFlow firstStepFlow = firstStepFlowOpt.get();

        

        // 找到該部門中符合該職位的第一位簽核人
        Optional<Employee> approverOpt = employeeRepository
            .findByPositionAndDepartment(firstStepFlow.getApproverPosition(), leaveRequest.getEmployee().getDepartment())
            .stream()
            .findFirst();
        System.out.println(approverOpt.get().getEmployeeName());
        if (!approverOpt.isPresent()) {
            throw new RuntimeException("找不到該部門的簽核人，請確認設定");
        }
        Employee approver = approverOpt.get();

        // 創建簽核步驟
        ApprovalStep approvalStep = new ApprovalStep();
        approvalStep.setFlow(firstStepFlow);
        approvalStep.setLeaveRequest(leaveRequest);
        approvalStep.setCurrentStep(1);
        approvalStep.setApprover(approver);
        approvalStep.setStatus(statusRepository.findByStatusName("待審核").orElse(null));
        approvalStep.setUpdatedAt(LocalDateTime.now());

        // 儲存簽核步驟
        approvalStepRepository.save(approvalStep);
        System.out.println("hehehe");
    }

    
    
    public String approveLeaveRequest(Integer approvalStepId, Integer approverUserId, Integer statusId, String comment) {
        Optional<ApprovalStep> stepOpt = approvalStepRepository.findById(approvalStepId);
        if (!stepOpt.isPresent()) {
            return "簽核步驟不存在";
        }
        ApprovalStep step = stepOpt.get();

        // 檢查是否是正確的審核人
        if (!step.getApprover().getEmployeeId().equals(approverUserId)) {
            return "你沒有權限審核這個請假單";
        }

        // 更新當前審核步驟狀態
        step.setStatus(statusRepository.findById(statusId).orElse(null));
        step.setComment(comment);
        step.setUpdatedAt(LocalDateTime.now());
        approvalStepRepository.save(step);

        // 透過 `ApprovalFlowRepository` 查找下一個審核步驟 
        ApprovalFlow currentFlow = step.getFlow();
        Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findApprovalFlow(
            step.getFlow().getPosition().getPositionId(), 
            step.getFlow().getRequestType().getId(), 
            currentFlow.getStepOrder() + 1
        );

        if (nextFlowOpt.isPresent()) {
            // 若有下一步驟，新增下一個 `ApprovalStep`
            ApprovalFlow nextFlow = nextFlowOpt.get();
            Optional<Employee> nextApproverOpt = employeeRepository
                    .findByPositionAndDepartment(nextFlow.getApproverPosition(), step.getApprover().getDepartment())
                    .stream()
                    .findFirst(); // 取第一位符合條件的員工
            if (!nextApproverOpt.isPresent()) {
                return "找不到該部門的審核人，請確認設定";
            }
            Employee nextApprover = nextApproverOpt.get();
            ApprovalStep nextStep = new ApprovalStep();
            nextStep.setFlow(nextFlow);
            nextStep.setLeaveRequest(step.getLeaveRequest());
            nextStep.setCurrentStep(nextFlow.getStepOrder());
            nextStep.setApprover(nextApprover);
            nextStep.setStatus(statusRepository.findByStatusName("待審核").get());
            nextStep.setUpdatedAt(LocalDateTime.now());

            approvalStepRepository.save(nextStep);
        } else {
            // 若沒有下一步，代表簽核完成，更新請假單狀態
            LeaveRequest leaveRequest = step.getLeaveRequest();
            leaveRequest.setStatus(statusRepository.findByStatusName("核准").get());
            leaveRequestRepository.save(leaveRequest);
        }

        return "簽核成功";
    }

}

