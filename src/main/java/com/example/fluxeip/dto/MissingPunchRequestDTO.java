package com.example.fluxeip.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissingPunchRequestDTO {

	private Integer employeeId;
	private Integer clockTypeId;
	private Date missingDate;
	private String reason;
	private Integer statusId;
	

}
