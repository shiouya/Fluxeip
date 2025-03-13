package com.example.fluxeip.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceViolationDTO {
    private String violationType;  // 異常類型
    private int violationMinutes;  // 異常分鐘數
    private LocalDateTime createdAt;  // 異常發生時間

}

