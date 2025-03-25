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
public class LeaveApprovalStepDTO {
//    private Integer id;
	private Integer stepId;
	private Integer leaveRequestId;
	private Integer requestEmployeeId;
    private String requestEmployeeName;
    private String leaveType; 
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private BigDecimal leaveHours;
    private String reason;
    private LocalDateTime submittedAt;
    private String attachmentName;
    private String attachmentPath;
    private Integer approverId;
    private String approverName;
    private String status;
    private Integer currentStep;
    private String comment;
    private LocalDateTime updatedAt;
    
}
