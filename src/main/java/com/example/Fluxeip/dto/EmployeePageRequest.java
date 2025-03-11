package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeePageRequest {
	
	private Integer current;
	private Integer rows;
	private String department;
	private String position;

}
