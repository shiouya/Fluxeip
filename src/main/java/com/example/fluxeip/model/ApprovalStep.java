package com.example.fluxeip.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_steps")
@Data
public class ApprovalStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "flow_id", nullable = false)
    private ApprovalFlow flow;

    @Column(name = "request_id", nullable = false)
    private Integer requestId;

    @Column(name = "current_step", nullable = false)
    private Integer currentStep;

    @ManyToOne
    @JoinColumn(name = "approver_user_id", nullable = false)
    private Employee approver;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "comment")
    private String comment;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
