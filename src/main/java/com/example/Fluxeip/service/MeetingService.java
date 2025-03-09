package com.example.fluxeip.service;

import com.example.fluxeip.dto.MeetingDTO;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Meeting;
import com.example.fluxeip.model.Room;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.MeetingRepository;
import com.example.fluxeip.repository.RoomRepository;
import com.example.fluxeip.repository.StatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private StatusRepository statusRepository;

    
    public List<MeetingDTO> findAll() {
        List<Meeting> meetings = meetingRepository.findAll();

        if (meetings.isEmpty()) {
            System.out.println("目前沒有任何會議");
        } else {
            System.out.println("成功查詢所有會議，共 " + meetings.size() + " 場");
        }

        return meetings.stream().map(MeetingDTO::new).collect(Collectors.toList());
    }


   
    public Optional<MeetingDTO> findById(Integer id) {
        if (id == null) {
            System.out.println("錯誤：meetingId 不能為 null");
            return Optional.empty();
        }

        Optional<Meeting> optional = meetingRepository.findById(id);

        if (optional.isPresent()) {
            System.out.println("成功查詢到 ID 為 " + id + " 的會議：" + optional.get().getTitle());
            return Optional.of(new MeetingDTO(optional.get()));
        } else {
            System.out.println("錯誤：找不到 ID 為 " + id + " 的會議");
            return Optional.empty();
        }
    }

    
    public List<MeetingDTO> findByRoomId(Integer roomId) {
        if (roomId == null) {
            System.out.println("錯誤：roomId 不能為 null");
            return new ArrayList<>();
        }

        List<Meeting> meetings = meetingRepository.findByRoomId(roomId);

        if (meetings.isEmpty()) {
            System.out.println("目前會議室 ID " + roomId + " 沒有任何會議");
        } else {
            System.out.println("成功查詢到會議室 ID 為 " + roomId + " 的會議，共 " + meetings.size() + " 場");
        }

        // **這裡改用 DTO 來回傳，100% 避免無限遞迴！**
        return meetings.stream().map(MeetingDTO::new).collect(Collectors.toList());
    }
    
    
   
    public Optional<MeetingDTO> create(MeetingDTO meetingDTO) {
        if (meetingDTO == null) {
            System.out.println("錯誤：meetingDTO 不能為 null");
            return Optional.empty();
        }

        // 取得 Employee、Room、Status（確保這些 ID 存在）
        Optional<Employee> employee = employeeRepository.findById(meetingDTO.getEmployeeId());
        Optional<Room> room = roomRepository.findById(meetingDTO.getRoomId());
        Optional<Status> status = statusRepository.findById(meetingDTO.getStatusId());

        if (employee.isEmpty() || room.isEmpty() || status.isEmpty()) {
            System.out.println("錯誤：員工、會議室或狀態 ID 無效");
            return Optional.empty();
        }

        // 建立 Meeting 物件
        Meeting meeting = new Meeting();
        meeting.setTitle(meetingDTO.getTitle());
        meeting.setNotes(meetingDTO.getNotes());
        meeting.setStartTime(meetingDTO.getStartTime());
        meeting.setEndTime(meetingDTO.getEndTime());
        meeting.setEmployee(employee.get());  
        meeting.setRoom(room.get());         
        meeting.setStatus(status.get());      

        // 存入資料庫
        Meeting insert = meetingRepository.save(meeting);
        System.out.println("成功新增會議：" + insert.getTitle());

        return Optional.of(new MeetingDTO(insert));
    }


   
    public Optional<MeetingDTO> update(Integer id, MeetingDTO meetingDTO) {
        if (id == null || meetingDTO == null) {
            System.out.println("錯誤：meetingId 或 meetingDTO 不能為 null");
            return Optional.empty();
        }

        Optional<Meeting> optional = meetingRepository.findById(id);

        if (optional.isEmpty()) {
            System.out.println("錯誤：找不到 ID 為 " + id + " 的會議");
            return Optional.empty();
        }

        Meeting existingMeeting = optional.get();

        // 更新欄位
        existingMeeting.setTitle(meetingDTO.getTitle());
        existingMeeting.setNotes(meetingDTO.getNotes());
        existingMeeting.setStartTime(meetingDTO.getStartTime());
        existingMeeting.setEndTime(meetingDTO.getEndTime());

        // 取得 Employee、Room、Status（確保這些 ID 存在）
        Optional<Employee> employeeOpt = employeeRepository.findById(meetingDTO.getEmployeeId());
        Optional<Room> roomOpt = roomRepository.findById(meetingDTO.getRoomId());
        Optional<Status> statusOpt = statusRepository.findById(meetingDTO.getStatusId());

        if (employeeOpt.isEmpty() || roomOpt.isEmpty() || statusOpt.isEmpty()) {
            System.out.println("錯誤：員工、會議室或狀態 ID 無效");
            return Optional.empty();
        }

        existingMeeting.setEmployee(employeeOpt.get());
        existingMeeting.setRoom(roomOpt.get());
        existingMeeting.setStatus(statusOpt.get());

        // 儲存修改後的 Meeting
        Meeting updatedMeeting = meetingRepository.save(existingMeeting);
        System.out.println("成功更新會議：" + updatedMeeting.getTitle());

        return Optional.of(new MeetingDTO(updatedMeeting));
    }



    
    public boolean delete(Integer id) {
        if (id == null) {
            System.out.println("錯誤：meetingId 不能為 null");
            return false;
        }

        Optional<Meeting> optional = meetingRepository.findById(id);

        if (optional.isEmpty()) {
            System.out.println("錯誤：找不到 ID 為 " + id + " 的會議");
            return false;
        }

        meetingRepository.deleteById(id);
        System.out.println("成功刪除會議 ID: " + id);
        return true;
    }


    
    public boolean exists(Integer id) {
        return id != null && meetingRepository.existsById(id);
    }
}
