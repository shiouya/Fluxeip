package com.example.fluxeip.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestResponseDTO {
	private Integer leaveRequestId;
	private String employeeName;
    private String leaveType; 
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Integer leaveHours;
    private String reason;
    private LocalDateTime submittedAt;
    private String attachmentName;
    private String attachmentPath;
    private String status;    
}
