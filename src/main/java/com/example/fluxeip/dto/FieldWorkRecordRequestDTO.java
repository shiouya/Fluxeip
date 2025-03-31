package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldWorkRecordRequestDTO {

	private Integer employeeId;
	private LocalDate fieldWorkDate;
	private BigDecimal totalHours;
    private String location;  
	private String purpose;
	private String status;
	

}
