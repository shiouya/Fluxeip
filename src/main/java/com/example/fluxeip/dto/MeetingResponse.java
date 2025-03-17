package com.example.fluxeip.dto;

import com.example.fluxeip.model.Meeting;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetingResponse {
	private Integer id;
	private String title;
	private String notes;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String employeeName;
	private String roomName;
	private String statusName;
	private String message; // 可選的錯誤訊息

	public MeetingResponse(Meeting meeting) {
		this.id = meeting.getId();
		this.title = meeting.getTitle();
		this.notes = meeting.getNotes();
		this.startTime = meeting.getStartTime();
		this.endTime = meeting.getEndTime();
		this.employeeName = meeting.getEmployee().getEmployeeName();
		this.roomName = meeting.getRoom().getRoomName();
		this.statusName = meeting.getStatus().getStatusName();
	}

	public MeetingResponse() {

	}

	// 用於回傳錯誤訊息
	public MeetingResponse(String message) {
		this.message = message;
	}
}
