package com.example.fluxeip.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleResponse {
	private int scheduleId;
	private String employeeName;
	private String departmentName;
	private String shiftTypeName;
	private LocalDate date;
}
