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
public class WorkAdjustmentRequestDTO {

	private Integer employeeId;
	private Integer adjustmentTypeId;
	private Date adjustmentDate;
	private BigDecimal hours;
	private String reason;
	private Integer statusId;
	

}
