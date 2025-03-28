package com.example.fluxeip.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissingPunchResponseDTO {

	private Integer missingPunchRequestId;
	private String employeeName;
	private LocalDate missingDate;
	private String clockType;
	private String reason;
	private LocalDateTime submittedAt;
	private String status;
	
	

}
