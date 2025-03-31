package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PersonalCalendarResponseDTO {

    private Integer id;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private String employeeId;
}

