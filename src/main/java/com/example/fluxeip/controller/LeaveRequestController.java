package com.example.fluxeip.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fluxeip.dto.LeaveRequestRequest;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.service.FileService;
import com.example.fluxeip.service.LeaveRequestService;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequestById(@PathVariable Integer id) {
        Optional<LeaveRequest> leaveRequest = leaveRequestService.getLeaveRequestById(id);
        return leaveRequest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitLeaveRequest(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("leaveTypeId") Integer leaveTypeId,
            @RequestParam("startDatetime") String startDatetime,
            @RequestParam("endDatetime") String endDatetime,
            @RequestParam("leaveHours") Integer leaveHours,
            @RequestParam("statusId") Integer statusId,
            @RequestParam("reason") String reason,
            @RequestParam(value = "attachments", required = false) MultipartFile attachments) {

        // 建立 DTO
        LeaveRequestRequest leaveRequestDTO = new LeaveRequestRequest();
        leaveRequestDTO.setEmployeeId(employeeId);
        leaveRequestDTO.setLeaveTypeId(leaveTypeId);
        leaveRequestDTO.setStartDatetime(LocalDateTime.parse(startDatetime));
        leaveRequestDTO.setEndDatetime(LocalDateTime.parse(endDatetime));
        leaveRequestDTO.setLeaveHours(leaveHours);
        leaveRequestDTO.setStatusId(statusId);
        leaveRequestDTO.setReason(reason);

        // 如果有附件，處理附件
        if (attachments != null) {
            String attachmentPath = fileService.saveFile(attachments); // 調用 service 儲存檔案
            leaveRequestDTO.setAttachments(attachmentPath); // 設置附件的路徑
        }

        // 交給 Service 層處理請假請求
        Object leaveRequest = leaveRequestService.createLeaveRequest(leaveRequestDTO);
        if (leaveRequest instanceof String) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(leaveRequest);
        }
        return ResponseEntity.ok(leaveRequest);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Integer id) {
        leaveRequestService.deleteLeaveRequest(id);
        return ResponseEntity.noContent().build();
    }
}
