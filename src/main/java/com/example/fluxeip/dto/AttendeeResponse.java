package com.example.fluxeip.dto;

import java.time.LocalDateTime;

import com.example.fluxeip.model.Attendee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeResponse {
    private Integer attendeeId;
    //員工資訊
    private Integer employeeId;
    private String employeeName;
    
    //是否參加
    private Boolean isAttending; 
    private LocalDateTime respondTime;
    
    
    // 會議資訊
    private Integer meetingId;
    private String meetingTitle;
    private LocalDateTime meetingStartTime;
    private LocalDateTime meetingEndTime;
    private String meetingRoomName;
    private String meetingHostName;
    private String statusName;
    
    
    private String message;
    
    public  AttendeeResponse(Attendee attendee) {
    	
    	this.attendeeId = attendee.getId();
    	this.employeeId = attendee.getEmployee().getEmployeeId();
    	this.employeeName = attendee.getEmployee().getEmployeeName();
    	this.isAttending = attendee.getIsAttending();
    	this.respondTime = attendee.getRespondTime();
    	
    	if(attendee.getMeeting() !=null) {
    		this.meetingId = attendee.getMeeting().getId();
    		this.meetingTitle = attendee.getMeeting().getTitle();
            this.meetingStartTime = attendee.getMeeting().getStartTime();
            this.meetingEndTime = attendee.getMeeting().getEndTime();
    	}
    	
    	if(attendee.getMeeting().getRoom() != null) {
    		this.meetingRoomName = attendee.getMeeting().getRoom().getRoomName();
    	}
    	
    	if(attendee.getMeeting().getEmployee() != null) {
    		this. meetingHostName = attendee.getMeeting().getEmployee().getEmployeeName();
    	}
    	  	
        if (attendee.getMeeting().getStatus() != null) {
            this.statusName = attendee.getMeeting().getStatus().getStatusName();
        }
    }
    
    public AttendeeResponse(String message) {
        this.message = message;
    }

}
