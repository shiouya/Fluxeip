package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequestDTO {
	private Integer employeeId;
	private Integer expenseTypeId;
	private BigDecimal amount;
	private String description;
	private Integer statusId;
	private String attachments;
}
