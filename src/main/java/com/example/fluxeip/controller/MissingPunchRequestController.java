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

import com.example.fluxeip.dto.MissingPunchRequestDTO;
import com.example.fluxeip.dto.MissingPunchResponseDTO;
import com.example.fluxeip.dto.WorkAdjustmentRequestDTO;
import com.example.fluxeip.dto.WorkAdjustmentResponseDTO;
import com.example.fluxeip.model.MissingPunchRequest;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.service.MissingPunchRequestService;

@RestController
@RequestMapping("/api/missing-punch")
public class MissingPunchRequestController {

	
	@Autowired
	private MissingPunchRequestService missingPunchRequestService;

    @GetMapping
    public ResponseEntity<List<MissingPunchRequest>> getAllRequests() {
        return ResponseEntity.ok(missingPunchRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissingPunchRequest> getRequestById(@PathVariable Integer id) {
        Optional<MissingPunchRequest> request = missingPunchRequestService.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getRequestsByEmployeeId(@PathVariable Integer employeeId) {
        List<MissingPunchResponseDTO> requestsByEmployeeId = missingPunchRequestService.getRequestsByEmployeeId(employeeId);
        if (requestsByEmployeeId != null && !requestsByEmployeeId.isEmpty()) {
            return ResponseEntity.ok(requestsByEmployeeId);
        } else {
            return ResponseEntity.noContent().build();  
        }
    	
    	
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody MissingPunchRequestDTO request) {
    	String missingPunchRequest = missingPunchRequestService.createRequest(request);
    	if(missingPunchRequest=="申請失敗") {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(missingPunchRequest);
    	}
    	if(missingPunchRequest=="啟動簽核流程時發生錯誤") {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(missingPunchRequest);
    	}
        return ResponseEntity.ok(missingPunchRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Integer id) {
    	missingPunchRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
