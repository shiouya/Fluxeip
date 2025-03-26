package com.example.fluxeip.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.LeaveRequestResponseDTO;
import com.example.fluxeip.dto.WorkAdjustmentRequestDTO;
import com.example.fluxeip.dto.WorkAdjustmentResponseDTO;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.service.WorkAdjustmentRequestService;

@RestController
@RequestMapping("/api/work-adjustments")
public class WorkAdjustmentRequestController {

    @Autowired
    private WorkAdjustmentRequestService workAdjustmentRequestService;

    @GetMapping
    public ResponseEntity<List<WorkAdjustmentRequest>> getAllRequests() {
        return ResponseEntity.ok(workAdjustmentRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkAdjustmentRequest> getRequestById(@PathVariable Integer id) {
        Optional<WorkAdjustmentRequest> request = workAdjustmentRequestService.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<WorkAdjustmentResponseDTO>> getRequestsByEmployeeId(@PathVariable Integer employeeId) {
        List<WorkAdjustmentResponseDTO> requestsByEmployeeId = workAdjustmentRequestService.getRequestsByEmployeeId(employeeId);
        if (requestsByEmployeeId != null && !requestsByEmployeeId.isEmpty()) {
            return ResponseEntity.ok(requestsByEmployeeId);
        } else {
            return ResponseEntity.noContent().build();  // 若沒有加班申請紀錄，返回 204 No Content
        }
    	
    	
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody WorkAdjustmentRequestDTO request) {
    	String workAdjustmentRequest = workAdjustmentRequestService.createRequest(request);
    	if(workAdjustmentRequest=="申請失敗") {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workAdjustmentRequest);
    	}
    	if(workAdjustmentRequest=="啟動簽核流程時發生錯誤") {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(workAdjustmentRequest);
    	}
        return ResponseEntity.ok(workAdjustmentRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Integer id) {
        workAdjustmentRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
