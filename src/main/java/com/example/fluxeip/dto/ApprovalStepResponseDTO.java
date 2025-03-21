package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStepResponseDTO {
   
	private Integer stepId;
	private Integer leaveRequestId;
	private Integer requestEmployeeId;
    private String requestEmployeeName;
    private Integer approverId;
    private String approverName;
    private String status;
    private Integer currentStep;
    private String comment;
    private LocalDateTime updatedAt;
    
}
