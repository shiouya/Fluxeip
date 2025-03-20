package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.ApprovalStep;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Integer> {
//    List<ApprovalStep> findByRequestIdOrderByCurrentStepAsc(Integer requestId);
    
    @Query("SELECT a FROM ApprovalStep a WHERE a.leaveRequest.id = :requestId ORDER BY a.currentStep ASC")
    List<ApprovalStep> findByRequestIdOrderByCurrentStepAsc(@Param("requestId") Integer requestId);
    
 // 根據審核人 ID 和狀態查詢待審核的請假單
    @Query("SELECT a FROM ApprovalStep a " +
           "WHERE a.approver.employeeId = :approverId " +
           "AND a.status.statusName = :status")
    List<ApprovalStep> findPendingApprovalSteps(@Param("approverId") Integer approverId, @Param("status") String status);
}
