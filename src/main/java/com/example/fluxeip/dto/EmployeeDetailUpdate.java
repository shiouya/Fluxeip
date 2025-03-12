package com.example.fluxeip.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDetailUpdate {

	private Integer employeeId;
	private String email;
	private String phone;
	private String address;
	private String emergencyContact;
	private String energencyPhone;

	private MultipartFile photoFile;

}
