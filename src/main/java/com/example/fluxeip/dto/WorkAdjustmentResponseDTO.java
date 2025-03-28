package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkAdjustmentResponseDTO {

	private Integer workAdjustmentRequestId;
	private String employeeName;
	private String adjustmentType;
	private Date adjustmentDate;
	private LocalDateTime submittedAt;
	private BigDecimal hours;
	private String reason;
	private String status;
	

}
