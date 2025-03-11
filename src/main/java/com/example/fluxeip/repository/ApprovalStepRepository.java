package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.ApprovalStep;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Integer> {
    List<ApprovalStep> findByRequestIdOrderByCurrentStepAsc(Integer requestId);
}
