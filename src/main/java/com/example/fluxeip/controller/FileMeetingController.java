package com.example.fluxeip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fluxeip.service.FileMeetingService;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*")
public class FileMeetingController {

    @Autowired
    private FileMeetingService fileMeetingService;
    
    

    @PostMapping("/{meetingId}/files")
    public ResponseEntity<?> uploadMeetingFile(
            @PathVariable Integer meetingId,
            @RequestParam("uploaderId") Integer uploaderId,
            @RequestParam("file") MultipartFile file) {

        boolean success = fileMeetingService.uploadFile(meetingId, uploaderId, file);

        if (success) {
            return ResponseEntity.ok("檔案上傳成功");
        } else {
            return ResponseEntity.badRequest().body("上傳失敗，請確認身分與檔案內容");
        }
    }
    
    
    @GetMapping("/{meetingId}/files")
    public ResponseEntity<?> getFilesByMeeting(@PathVariable Integer meetingId) {
        List<Map<String, Object>> fileList = fileMeetingService.getFilesByMeetingId(meetingId);

        if (fileList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fileList);
    }
    
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteMeetingFile(
            @PathVariable Integer fileId,
            @RequestParam("requesterId") Integer requesterId) {

        boolean deleted = fileMeetingService.deleteFile(fileId, requesterId);

        if (deleted) {
            return ResponseEntity.ok("檔案已刪除");
        } else {
            return ResponseEntity.status(403).body("刪除失敗：無權限或檔案不存在");
        }
    }

}
