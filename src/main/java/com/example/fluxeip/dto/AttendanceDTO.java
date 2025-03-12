package com.example.fluxeip.dto;

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
    private double totalHours;
    private double regularHours;
    private double overtimeHours;
    private double fieldWorkHours;
    private boolean hasViolation;
    private List<AttendanceLogDTO> attendanceLogs;
    private List<AttendanceViolationDTO> attendanceViolations;


}

