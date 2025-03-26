package com.example.fluxeip.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
import com.example.fluxeip.dto.LeaveRequestResponseDTO;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.service.ApprovalFlowService;
import com.example.fluxeip.service.FileService;
import com.example.fluxeip.service.LeaveRequestService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private ApprovalFlowService approvalFlowService;  // 注入簽核流程 Service
    

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable Integer id) {
        LeaveRequestResponseDTO leaveRequestById = leaveRequestService.getLeaveRequestById(id);
        if (leaveRequestById != null) {
            return ResponseEntity.ok(leaveRequestById);
        } else {
            return ResponseEntity.noContent().build();  // 若沒有請假紀錄，返回 204 No Content
        }
    }
    
    
    // 根據員工 ID 查詢所有請假申請
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequestsByEmployeeId(@PathVariable Integer employeeId) {
        List<LeaveRequestResponseDTO> leaveRequestDTOs = leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
        if (leaveRequestDTOs != null && !leaveRequestDTOs.isEmpty()) {
            return ResponseEntity.ok(leaveRequestDTOs);
        } else {
            return ResponseEntity.noContent().build();  // 若沒有請假紀錄，返回 204 No Content
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitLeaveRequest(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("leaveTypeId") Integer leaveTypeId,
            @RequestParam("startDatetime") String startDatetime,
            @RequestParam("endDatetime") String endDatetime,
            @RequestParam("leaveHours") BigDecimal leaveHours,
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
        
        
        // 啟動請假單的簽核流程
        try {
            LeaveRequest request = (LeaveRequest) leaveRequest;
            approvalFlowService.startLeaveApprovalProcess(request);  // 呼叫簽核服務啟動流程
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("啟動簽核流程時發生錯誤: " + e.getMessage());
        }
        
        return ResponseEntity.ok(leaveRequest);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Integer id) {
        leaveRequestService.deleteLeaveRequest(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
    @GetMapping("/attachments/**")
    public ResponseEntity<Resource> downloadAttachment(HttpServletRequest request) {
        // 取得 URL 中的路徑部分（去掉 API 路徑前綴）
        String filePath = request.getRequestURI().replace("/api/leave-requests/attachments/", "");

        try {
            // 解碼 URL，以處理空格和特殊字符
            filePath = URLDecoder.decode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // 確保使用正確的路徑分隔符
        Path filePathResolved = Paths.get(filePath.replace("/", File.separator)).normalize();
        File file = filePathResolved.toFile();

        if (file.exists() && file.canRead()) {
            try {
                // 生成 Resource 物件
                Resource resource = new UrlResource(file.toURI());

                // 嘗試使用 Files.probeContentType() 自動判斷檔案的 MIME 類型
                String contentType = Files.probeContentType(filePathResolved);
                if (contentType == null) {
                    // 強制設定 MIME 類型
                    if (file.getName().endsWith(".docx")) {
                        contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    } else if (file.getName().endsWith(".pdf")) {
                        contentType = "application/pdf";
                    } else if (file.getName().endsWith(".txt")) {
                        contentType = "text/plain";
                    } else {
                        contentType = "application/octet-stream";
                    }
                }

                // 返回檔案並設置下載標頭
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))  // 設置 MIME 類型
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } catch (MalformedURLException e) {
                System.out.println("錯誤: " + e.getMessage());
                return ResponseEntity.notFound().build();
            } catch (IOException e) {
                System.out.println("檔案類型處理錯誤: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            System.out.println("檔案不存在或無法讀取：" + file);
            return ResponseEntity.notFound().build();
        }
    }




}
