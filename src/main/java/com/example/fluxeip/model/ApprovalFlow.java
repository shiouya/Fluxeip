package com.example.fluxeip.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_flows")
@Data
public class ApprovalFlow {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "flow_name", nullable = false)
    private String flowName;

    @Column(name = "request_type_id", nullable = false)
    private Integer requestTypeId;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @ManyToOne
    @JoinColumn(name = "approver_position_id", nullable = false)
    private Position approverPosition;

    @ManyToOne
    @JoinColumn(name = "next_step_id")
    private ApprovalFlow nextStep;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
