package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeUpdateRequest {

	private Integer employeeId;
	private String employeeName;
	private String department;
	private String position;
	private String status;

}
