package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactsDto {
	
	private byte[] photo;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private String department;
	
	private String position;
	
	private Integer empId;
}
