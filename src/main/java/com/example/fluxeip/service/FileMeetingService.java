package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.FileMeeting;
import com.example.fluxeip.model.Meeting;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.FileMeetingRepository;
import com.example.fluxeip.repository.MeetingRepository;

@Service
public class FileMeetingService {

    @Autowired
    private FileMeetingRepository fileMeetingRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileService fileService; 

    public boolean uploadFile(Integer meetingId, Integer uploaderId, MultipartFile file) {
        if (file.isEmpty()) return false;

        Optional<Meeting> optMeeting = meetingRepository.findById(meetingId);
        Optional<Employee> optUploader = employeeRepository.findById(uploaderId);

        if (optMeeting.isEmpty() || optUploader.isEmpty()) return false;

        Meeting meeting = optMeeting.get();
        if (!meeting.getEmployee().getEmployeeId().equals(uploaderId)) {
            return false;
        }

        try {
            // 使用共用檔案服務儲存檔案並取得路徑
            String savedPath = fileService.saveFile(file);

            // 儲存到資料庫
            FileMeeting fileMeeting = new FileMeeting();
            fileMeeting.setMeeting(meeting);
            fileMeeting.setEmployee(optUploader.get());
            fileMeeting.setFilesName(file.getOriginalFilename());
            fileMeeting.setFilesPath(savedPath);
            fileMeeting.setUploadTime(LocalDateTime.now());

            fileMeetingRepository.save(fileMeeting);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //這場會議的所有檔案
    public List<Map<String, Object>> getFilesByMeetingId(Integer meetingId) {
       
        List<FileMeeting> files = fileMeetingRepository.findByMeetingId(meetingId);

       
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

       
        List<Map<String, Object>> result = new ArrayList<>();

        
        for (FileMeeting file : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", file.getId()); 
            map.put("filesName", file.getFilesName()); 
            map.put("filesPath", file.getFilesPath()); 
            map.put("uploadTime", file.getUploadTime());

            
            if (file.getEmployee() != null) {
                map.put("uploaderId", file.getEmployee().getEmployeeId());
                map.put("uploaderName", file.getEmployee().getEmployeeName());
            } else {
                map.put("uploaderId", null);
                map.put("uploaderName", "未知");
            }

            
            result.add(map);
        }

        
        return result;
    }
    
    // 刪除
    public boolean deleteFile(Integer fileId, Integer requesterId) {
        Optional<FileMeeting> optionalFile = fileMeetingRepository.findById(fileId);

        if (optionalFile.isEmpty()) return false;

        FileMeeting fileMeeting = optionalFile.get();

        
        Integer uploaderId = fileMeeting.getEmployee().getEmployeeId();
        Integer hostId = fileMeeting.getMeeting().getEmployee().getEmployeeId();

        if (!requesterId.equals(uploaderId) && !requesterId.equals(hostId)) {
            return false; 
        }

        try {
            
            fileService.deleteFile(fileMeeting.getFilesPath());

  
            fileMeetingRepository.deleteById(fileId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
