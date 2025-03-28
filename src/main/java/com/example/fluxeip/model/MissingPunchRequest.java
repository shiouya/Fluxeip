package com.example.fluxeip.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "missing_punch_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissingPunchRequest {
	
    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "missing_date", nullable = false)
    private LocalDate missingDate;
    
    @ManyToOne
    @JoinColumn(name = "clock_type_id", nullable = false)
    private Type clockType;
    
    @Column(name = "reason")
    private String reason;
    
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;
    
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
}