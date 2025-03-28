package com.example.fluxeip.dto;

import java.time.LocalDate;

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
	private LocalDate missingDate;
	private String reason;
	private Integer statusId;
	

}
