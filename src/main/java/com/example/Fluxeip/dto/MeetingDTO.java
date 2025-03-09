package com.example.fluxeip.dto;

import java.time.LocalDateTime;

import com.example.fluxeip.model.Meeting;

import lombok.Getter;

@Getter
public class MeetingDTO {
    private Integer id;
    private String title;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer employeeId;
    private String employeeName;
    private Integer roomId;
    private String roomName;
    private Integer statusId;
    private String statusName;
    
    public MeetingDTO() {
    	
    }

    public MeetingDTO(Meeting meeting) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.notes = meeting.getNotes();
        this.startTime = meeting.getStartTime();
        this.endTime = meeting.getEndTime();
        this.employeeId = meeting.getEmployee().getEmployeeId();
        this.employeeName = meeting.getEmployee().getEmployeeName();
        this.roomId = meeting.getRoom().getId();
        this.roomName = meeting.getRoom().getRoomName();
        this.statusId = meeting.getStatus().getStatusId();
        this.statusName = meeting.getStatus().getStatusName();
    }


}
