package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginResponse {
	private Boolean success;
	private String message;

	private String token;
	private Integer employeeId;
	private String photo;
	private String employeeName;
	private String roleName;
	private String department;


}
