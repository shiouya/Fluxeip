package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private LocalDateTime createdAt;
    private BigDecimal totalHours;
    private BigDecimal regularHours;
    private BigDecimal overtimeHours;
    private BigDecimal fieldWorkHours;
    private boolean hasViolation;
    private List<AttendanceLogDTO> attendanceLogs;
    private List<AttendanceViolationDTO> attendanceViolations;


}

