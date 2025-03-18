package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.time.LocalTime;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShiftTypeResponse {
    private Integer shiftTypeId;
    private String departmentName;
    private String shiftName;
    private String shiftCategory;
    private LocalTime startTime;
    private LocalTime finishTime;
    private BigDecimal estimatedHours;

}
