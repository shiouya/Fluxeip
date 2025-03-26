package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkAdjustApprovalStepDTO {
//    private Integer id;
	private Integer stepId;
	private Integer requestId;
	private Integer requestEmployeeId;
    private String requestEmployeeName;
    private String type; 
    private Date adjustmentDate;
    private BigDecimal hours;
    private String reason;
    private LocalDateTime submittedAt;
    private Integer approverId;
    private String approverName;
    private String status;
    private Integer currentStep;
    private String comment;
    private LocalDateTime updatedAt;
    
}
