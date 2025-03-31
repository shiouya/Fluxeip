package com.example.fluxeip.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "shift_type")
public class ShiftType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_type_id", nullable = false)
    private Integer shiftTypeId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "shift_name", nullable = false, length = 50)
    private String shiftName;

    @Column(name = "shift_category", nullable = false, length = 10)
    private String shiftCategory;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "finish_time", nullable = false)
    private LocalTime finishTime;

    @Column(name = "estimated_hours", nullable = false)
    private BigDecimal estimatedHours;

    @JsonIgnore
    @OneToMany(mappedBy = "shiftType", cascade = CascadeType.ALL)
    private List<Schedule> schedules;
    
    @Column(name = "IsActive", nullable = false)
    private boolean isActive;
}