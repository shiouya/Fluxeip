package com.example.fluxeip.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.ApprovalFlowDTO;
import com.example.fluxeip.dto.ApprovalStepResponseDTO;
import com.example.fluxeip.dto.LeaveApprovalStepDTO;
import com.example.fluxeip.dto.MissingPunchApprovalStepDTO;
import com.example.fluxeip.dto.WorkAdjustApprovalStepDTO;
import com.example.fluxeip.service.ApprovalFlowService;
import com.example.fluxeip.service.ApprovalService;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {
    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalFlowService approvalFlowService;
    
    
//    // 取得特定請求的簽核流程
//    @GetMapping("/flow/{typeId}")
//    public ResponseEntity<?> getApprovalFlow(@PathVariable Integer typeId) {
//        return ResponseEntity.ok(approvalService.getApprovalFlowForType(typeId));
//    }
    

    // 取得請假請求的所有簽核步驟
    @GetMapping("/leave/steps/{requestId}")
    public ResponseEntity<?> getLeaveApprovalSteps(@PathVariable Integer requestId) {
        List<ApprovalStepResponseDTO> approvalStepsByRequestId = approvalService.getLeaveApprovalStepsByRequestId(requestId);
        return ResponseEntity.ok(approvalStepsByRequestId);
    }
    // 取得加減班請求的所有簽核步驟
    @GetMapping("/workadjust/steps/{requestId}")
    public ResponseEntity<?> getWorkadjustApprovalSteps(@PathVariable Integer requestId) {
    	List<ApprovalStepResponseDTO> approvalStepsByRequestId = approvalService.getWorkadjustApprovalStepsByRequestId(requestId);
    	return ResponseEntity.ok(approvalStepsByRequestId);
    }
    // 取得補卡請求的所有簽核步驟
    @GetMapping("/missingpunch/steps/{requestId}")
    public ResponseEntity<?> getMissingPunchApprovalSteps(@PathVariable Integer requestId) {
    	List<ApprovalStepResponseDTO> approvalStepsByRequestId = approvalService.getMissingPunchApprovalStepsByRequestId(requestId);
    	return ResponseEntity.ok(approvalStepsByRequestId);
    }
    
    
    // 請假簽核進行中
    @PutMapping("/leave/step/{stepId}/review")
    public ResponseEntity<String> leaveApproveOrRejectStep(
            @PathVariable Integer stepId,
            @RequestParam Integer approverId,
            @RequestParam String status,
            @RequestParam(required = false) String comment) {
        
        String result = approvalFlowService.approveLeaveRequest(stepId, approverId, status, comment);
        
        if ("簽核成功".equals(result)) {
            return ResponseEntity.ok(result);
        }else if("已否決請假單".equals(result)){
        	return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result); 
        }
    }
    
    //加減班簽核進行中
    @PutMapping("/workadjust/step/{stepId}/review")
    public ResponseEntity<String> workadjustApproveOrRejectStep(
    		@PathVariable Integer stepId,
    		@RequestParam Integer approverId,
    		@RequestParam String status,
    		@RequestParam(required = false) String comment) {
    	
    	String result = approvalFlowService.approveWorkAdjustmentRequest(stepId, approverId, status, comment);
    	
    	if ("簽核成功".equals(result)) {
    		return ResponseEntity.ok(result);
    	}else if("已否決請假單".equals(result)){
    		return ResponseEntity.ok(result);
    	} else {
    		return ResponseEntity.badRequest().body(result); 
    	}
    }

    // 查詢當前審核人待審核的請假單
    @GetMapping("/leave/pending/{approverId}")
    public ResponseEntity<List<LeaveApprovalStepDTO>> getLeavePendingApprovals(@PathVariable Integer approverId) {
        List<LeaveApprovalStepDTO> pendingApprovals = approvalFlowService.getPendingApprovalSteps(approverId);
        if (pendingApprovals.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(pendingApprovals);
        }
    }
    // 查詢當前審核人待審核的加減班單
    @GetMapping("/workadjust/pending/{approverId}")
    public ResponseEntity<List<WorkAdjustApprovalStepDTO>> getWorkAdjustPendingApprovals(@PathVariable Integer approverId) {
    	List<WorkAdjustApprovalStepDTO> pendingApprovals = approvalFlowService.getPendingWorkAdjustApprovalSteps(approverId);
    	if (pendingApprovals.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	} else {
    		return ResponseEntity.ok(pendingApprovals);
    	}
    }
    
    // 查詢當前審核人待審核的補卡單
    @GetMapping("/missingpunch/pending/{approverId}")
    public ResponseEntity<List<MissingPunchApprovalStepDTO>> getMissingPunchPendingApprovals(@PathVariable Integer approverId) {
    	List<MissingPunchApprovalStepDTO> pendingApprovals = approvalFlowService.getPendingMissingPunchApprovalSteps(approverId);
    	if (pendingApprovals.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	} else {
    		return ResponseEntity.ok(pendingApprovals);
    	}
    }
    

//    // 建立自訂簽核步驟
//    @PostMapping("/create/approval-flows")
//    public ResponseEntity<?> createApprovalFlow(@RequestBody List<ApprovalFlowDTO> flowSteps) {
//        return approvalFlowService.createApprovalFlow(flowSteps);
//    }
//
//    // 刪除簽核流程（包含所有後續步驟）
//    @DeleteMapping("/delete/approval-flows/{flowId}")
//    public ResponseEntity<?> deleteApprovalFlowAndNextSteps(@PathVariable Integer flowId) {
//        return approvalFlowService.deleteApprovalFlowAndNextSteps(flowId);
//    }
 
}

