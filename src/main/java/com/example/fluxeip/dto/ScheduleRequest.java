package com.example.fluxeip.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleRequest {

	private Integer employeeId;
	private String departmentName;
	private Integer shiftTypeId;
	private LocalDate date;
}
