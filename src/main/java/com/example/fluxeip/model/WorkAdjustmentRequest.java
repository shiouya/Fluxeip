package com.example.fluxeip.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_adjustment_requests")
public class WorkAdjustmentRequest {
    @Id
    @JoinColumn(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "adjustment_type_id", nullable = false)
    private Type adjustmentType;

    @Column(name = "adjustment_date", nullable = false)
    private Date adjustmentDate;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal hours;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();
}
