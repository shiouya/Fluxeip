package com.example.fluxeip.dto;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShiftTypeRequest {

	private String shiftName;
	private String departmentName;
	private String shiftCategory;
	private LocalTime startTime;
	private LocalTime finishTime;
}
