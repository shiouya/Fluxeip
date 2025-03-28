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
public class MissingPunchApprovalStepDTO {
//    private Integer id;
	private Integer stepId;
	private Integer requestId;
	private Integer requestEmployeeId;
    private String requestEmployeeName;
    private String type; 
    private LocalDate missingDate;
    private String reason;
    private LocalDateTime submittedAt;
    private Integer approverId;
    private String approverName;
    private String status;
    private Integer currentStep;
    private String comment;
    private LocalDateTime updatedAt;
    
}
