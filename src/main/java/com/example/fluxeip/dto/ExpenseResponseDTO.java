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
public class ExpenseResponseDTO {
	private Integer expenseRequestId;
	private String employeeName;
    private String expenseType; 
    private BigDecimal amount;
    private String description;
    private LocalDateTime submittedAt;
    private String attachmentName;
    private String attachmentPath;
    private String status;    
}
