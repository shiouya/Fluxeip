package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.EmployeeApprovalFlowDTO;
import com.example.fluxeip.dto.EmployeeApprovalFlowResponseDTO;
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
			return ResponseEntity.badRequest().body("指派員工自訂簽核流程設置失敗: " + e.getMessage());
		}
	}
	
	// 查詢員工自訂簽核流程
	@GetMapping("/{employeeId}")
	public ResponseEntity<?> getEmployeeApprovalFlowByEmployeeId(@PathVariable Integer employeeId) {
		try {
			List<EmployeeApprovalFlowResponseDTO> approvalFlowsByEmployeeId = employeeApprovalFlowService.getEmployeeApprovalFlowsByEmployeeId(employeeId);
			return ResponseEntity.ok(approvalFlowsByEmployeeId);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("查詢員工自訂簽核流程失敗: " + e.getMessage());
		}
	}
	
    // 解除綁定
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployeeApprovalFlowById(@PathVariable Integer id) {
    	
    	
    	try {
    		String deleteEmployeeApprovalFlowsById = employeeApprovalFlowService.deleteEmployeeApprovalFlowsById(id);
			return ResponseEntity.ok(deleteEmployeeApprovalFlowsById);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("解綁失敗: " + e.getMessage());
		}
    }
    
    
}
