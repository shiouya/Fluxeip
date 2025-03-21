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
        		step.getId(),
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
        // 取得員工的職位
        Integer positionId = leaveRequest.getEmployee().getPosition().getPositionId();
        Integer requestTypeId = leaveRequest.getLeaveType().getId();
        // 查詢對應的第一步驟簽核流程
        Optional<ApprovalFlow> firstStepFlowOpt = approvalFlowRepository
            .findApprovalFlow(positionId, requestTypeId, 1);
        
        if (!firstStepFlowOpt.isPresent()) {
            throw new RuntimeException("未找到對應的簽核流程");
        }
        ApprovalFlow firstStepFlow = firstStepFlowOpt.get();


        // 找到該部門中符合該職位的第一位簽核人
        Optional<Employee> approverOpt = employeeRepository
            .findTopByPositionAndDepartmentAndStatus(firstStepFlow.getApproverPosition(), leaveRequest.getEmployee().getDepartment(),statusRepository.findByStatusName("在職").get())
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
        approvalStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核","表單狀態").orElse(null));
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
        step.setStatus(statusRepository.findByStatusNameAndStatusType(statusName,"表單狀態").orElseThrow(() -> new RuntimeException("狀態不存在")));
        step.setComment(comment);
        step.setUpdatedAt(LocalDateTime.now());
        approvalStepRepository.save(step);

        // **✅ 如果否決，直接更新請假單狀態**
        if ("未核准".equals(step.getStatus().getStatusName())) {
            LeaveRequest leaveRequest = step.getLeaveRequest();
            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("未核准","表單狀態")
                    .orElseThrow(() -> new RuntimeException("狀態不存在")));
            
            leaveRequestRepository.save(leaveRequest);
            return "已否決請假單";
        }

        // **✅ 否則進入「核准」流程**
        ApprovalFlow currentFlow = step.getFlow();
        Optional<ApprovalFlow> nextFlowOpt = approvalFlowRepository.findApprovalFlow(
            currentFlow.getPosition().getPositionId(), 
            currentFlow.getRequestType().getId(), 
            currentFlow.getStepOrder() + 1
        );

        if (nextFlowOpt.isPresent()) {
            // 若有下一步驟，新增下一個 `ApprovalStep`
        	LeaveRequest leaveRequest = step.getLeaveRequest();
            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("審核中","表單狀態").orElseThrow(() -> new RuntimeException("狀態不存在")));
            leaveRequestRepository.save(leaveRequest);
            ApprovalFlow nextFlow = nextFlowOpt.get();
            Employee nextApprover = employeeRepository
                    .findTopByPositionAndDepartmentAndStatus(nextFlow.getApproverPosition(), step.getApprover().getDepartment(),statusRepository.findByStatusName("在職").get())
                    .orElseThrow(() -> new RuntimeException("找不到該部門的審核人，請確認設定"));

            ApprovalStep nextStep = new ApprovalStep();
            nextStep.setFlow(nextFlow);
            nextStep.setLeaveRequest(step.getLeaveRequest());
            nextStep.setCurrentStep(nextFlow.getStepOrder());
            nextStep.setApprover(nextApprover);
            nextStep.setStatus(statusRepository.findByStatusNameAndStatusType("待審核","表單狀態").orElseThrow(() -> new RuntimeException("狀態不存在")));
            nextStep.setUpdatedAt(LocalDateTime.now());

            approvalStepRepository.save(nextStep);
        } else {
            // 若沒有下一步，代表簽核完成，更新請假單狀態
            LeaveRequest leaveRequest = step.getLeaveRequest();
            leaveRequest.setStatus(statusRepository.findByStatusNameAndStatusType("已核決","表單狀態").orElseThrow(() -> new RuntimeException("狀態不存在")));
            leaveRequestRepository.save(leaveRequest);
        }

        return "簽核成功";
    }



}

