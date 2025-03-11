package com.example.fluxeip.service;

import com.example.fluxeip.model.*;
import com.example.fluxeip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ApprovalService {
    @Autowired
    private ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    private ApprovalStepRepository approvalStepRepository;

    @Autowired
    private EmployeeApprovalFlowRepository employeeApprovalFlowRepository;

    // 取得某類請求的簽核流程
    public List<ApprovalFlow> getApprovalFlowForType(Integer requestTypeId) {
        return approvalFlowRepository.findByRequestTypeIdOrderByStepOrderAsc(requestTypeId);
    }

    // 建立新的簽核步驟
    public ApprovalStep createApprovalStep(Integer requestId, Integer flowId, Employee approver) {
        ApprovalStep step = new ApprovalStep();
        step.setRequestId(requestId);
        step.setFlow(approvalFlowRepository.findById(flowId).orElseThrow());
        step.setApprover(approver);
        step.setStatus(new Status(1, "Pending","all_approval"));
        return approvalStepRepository.save(step);
    }

    // 取得申請單目前的簽核步驟
    public List<ApprovalStep> getApprovalStepsByRequestId(Integer requestId) {
        return approvalStepRepository.findByRequestIdOrderByCurrentStepAsc(requestId);
    }

    // 更新簽核狀態
    public ApprovalStep updateApprovalStep(Integer stepId, Integer statusId, String comment) {
        Optional<ApprovalStep> optionalStep = approvalStepRepository.findById(stepId);
        if (optionalStep.isPresent()) {
            ApprovalStep step = optionalStep.get();
            step.setStatus(new Status(statusId, statusId == 2 ? "Approved" : "Rejected","all_approval"));
            step.setComment(comment);
            return approvalStepRepository.save(step);
        }
        return null; 
    }
}

