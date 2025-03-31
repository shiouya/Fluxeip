package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.ApprovalFlowDTO;
import com.example.fluxeip.dto.ApprovalFlowResponseDTO;
import com.example.fluxeip.service.ApprovalFlowService;
import com.example.fluxeip.service.ApprovalService;

@RestController
@RequestMapping("/api/approval")
public class ApprovalFlowController {
	@Autowired
	private ApprovalService approvalService;

	@Autowired
	private ApprovalFlowService approvalFlowService;

    // 取得全部簽核步驟1的簽核流程
    @GetMapping("/flow/stepone/all")
    public ResponseEntity<?> getAllStepOneApprovalFlow() {
    	return ResponseEntity.ok(approvalFlowService.getAllStepOneApprovalFlow());
    }

	// 取得全部簽核步驟1的簽核流程
	@GetMapping("/flow/stepone")
	public ResponseEntity<?> getStepOneApprovalFlowByPositionAndRequestType(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) String position, @RequestParam(required = false) String requestType) {

		Page<ApprovalFlowResponseDTO> result = approvalFlowService.getFilteredApprovalFlows(page, size, search,
				position, requestType);
		return ResponseEntity.ok(result);
	}

	// 從簽核1取得後續的簽核流程
	@GetMapping("/flow/stepone/{flowId}")
	public ResponseEntity<?> getApprovalFlowByStepOne(@PathVariable Integer flowId) {
		System.out.println("有喔");
		return ResponseEntity.ok(approvalFlowService.getApprovalFlowAndNextSteps(flowId));
	}

	// 取得全部的簽核流程
	@GetMapping("/flow/all")
	public ResponseEntity<?> getAllApprovalFlow() {
		return ResponseEntity.ok(approvalFlowService.getAllApprovalFlow());
	}

	// 取得特定請求的簽核流程
	@GetMapping("/flow/{typeId}")
	public ResponseEntity<?> getApprovalFlow(@PathVariable Integer typeId) {
		return ResponseEntity.ok(approvalService.getApprovalFlowForType(typeId));
	}

	// 建立自訂簽核步驟
	@PostMapping("/create/approval-flows")
	public ResponseEntity<?> createApprovalFlow(@RequestBody List<ApprovalFlowDTO> flowSteps) {
		return approvalFlowService.createApprovalFlow(flowSteps);
	}

	// 刪除簽核流程（包含所有後續步驟）
	@DeleteMapping("/delete/approval-flows/{flowId}")
	public ResponseEntity<?> deleteApprovalFlowAndNextSteps(@PathVariable Integer flowId) {
		return approvalFlowService.deleteApprovalFlowAndNextSteps(flowId);
	}

}
