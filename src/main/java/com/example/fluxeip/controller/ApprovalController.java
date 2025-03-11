package com.example.fluxeip.controller;

import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {
    @Autowired
    private ApprovalService approvalService;

    // 取得特定請求的簽核流程
    @GetMapping("/flow/{typeId}")
    public ResponseEntity<?> getApprovalFlow(@PathVariable Integer typeId) {
        return ResponseEntity.ok(approvalService.getApprovalFlowForType(typeId));
    }

    // 建立新的簽核步驟
    @PostMapping("/step")
    public ResponseEntity<?> createApprovalStep(@RequestParam Integer requestId, @RequestParam Integer flowId, @RequestParam Integer approverId) {
        ApprovalStep step = approvalService.createApprovalStep(requestId, flowId, new Employee(approverId));
        return ResponseEntity.ok(step);
    }

    // 取得某請求的所有簽核步驟
    @GetMapping("/steps/{requestId}")
    public ResponseEntity<?> getApprovalSteps(@PathVariable Integer requestId) {
        List<ApprovalStep> steps = approvalService.getApprovalStepsByRequestId(requestId);
        return ResponseEntity.ok(steps);
    }

    // 更新簽核狀態
    @PutMapping("/step/{stepId}")
    public ResponseEntity<?> updateApprovalStep(@PathVariable Integer stepId, @RequestParam Integer statusId, @RequestParam String comment) {
        ApprovalStep step = approvalService.updateApprovalStep(stepId, statusId, comment);
        return step != null ? ResponseEntity.ok(step) : ResponseEntity.badRequest().body("簽核步驟不存在");
    }
}

