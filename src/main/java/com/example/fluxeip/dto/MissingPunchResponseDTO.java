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
public class MissingPunchResponseDTO {

	private Integer missingPunchRequestId;
	private String employeeName;
	private Date missingDate;
	private String clockType;
	private String reason;
	private String status;
	

}
