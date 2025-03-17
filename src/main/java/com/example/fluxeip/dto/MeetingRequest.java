package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingRequest {
    private String title;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer employeeId;
    private Integer roomId;
}