package com.example.fluxeip.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EmployeeCreateRequest {

	private String employeeName;
	private String positionName;
	private String departmentName;
	private Date hireDate;
	private String gender;
	private String identityCard;
	private String email;
	private String phone;
}
