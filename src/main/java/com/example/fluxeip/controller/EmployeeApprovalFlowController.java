package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.EmployeeApprovalFlowDTO;
import com.example.fluxeip.service.EmployeeApprovalFlowService;

@RestController 
@RequestMapping("/api/employee-approval-flows")
public class EmployeeApprovalFlowController {

	@Autowired
	private EmployeeApprovalFlowService employeeApprovalFlowService;

	// 設定員工自訂簽核流程
	@PostMapping("/create")
	public ResponseEntity<?> createEmployeeApprovalFlow(@RequestBody List<EmployeeApprovalFlowDTO> dto) {
		try {
			employeeApprovalFlowService.createEmployeeApprovalFlows(dto);
			return ResponseEntity.ok("指派員工自訂簽核流程設置成功");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("指派員工自訂簽核流程設置失敗: " + e.getMessage());
		}
	}
}
