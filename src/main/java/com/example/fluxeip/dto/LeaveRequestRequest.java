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
public class LeaveRequestRequest {
	private Integer employeeId;
	private Integer leaveTypeId;
	private LocalDateTime startDatetime;
	private LocalDateTime endDatetime;
	private BigDecimal leaveHours;
	private String reason;
	private Integer statusId;
	private String attachments;
}
