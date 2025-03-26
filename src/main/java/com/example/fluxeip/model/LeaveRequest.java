package com.example.fluxeip.model;

import java.math.BigDecimal;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leave_requests")  // 指定資料表名稱
public class LeaveRequest{

 
	@Id
	@JoinColumn(name = "id", nullable = false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee; 
	
    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private Type leaveType; 

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "leave_hours", nullable = false)
    private BigDecimal leaveHours;

    @Column(name = "reason")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(name = "attachments")
    private String attachments; 
}
