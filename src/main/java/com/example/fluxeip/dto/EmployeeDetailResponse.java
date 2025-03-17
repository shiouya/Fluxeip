package com.example.fluxeip.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDetailResponse {

	private Integer employeeId;
	private String employeeName;
	private String position;
	private String department;
	private Date hireDate;
	private String gender;
	private Date birthday;
	private String identityCard;
	private String email;
	private String phone;
	private String employeePhoto;
	private String address;
	private String emergencyContact;
	private String energencyPhone;
	private String status;

}
