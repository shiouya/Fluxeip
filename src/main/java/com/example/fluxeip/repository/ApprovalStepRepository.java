package com.example.fluxeip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.ApprovalStep;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Integer> {

    // 查詢 ApprovalStep 並過濾請求 ID，使用具體類型的字段
    @Query("SELECT a FROM ApprovalStep a WHERE a.requestId = :requestId ORDER BY a.currentStep ASC")
    List<ApprovalStep> findByRequestIdOrderByCurrentStepAsc(@Param("requestId") Integer requestId);

    // 根據審核人 ID 和狀態查詢待審核的請假單
    @Query("SELECT a FROM ApprovalStep a " +
           "WHERE a.approver.employeeId = :approverId " +
           "AND a.status.statusName = :status")
    List<ApprovalStep> findPendingApprovalSteps(@Param("approverId") Integer approverId, @Param("status") String status);

    // 查詢 ApprovalStep 並過濾為 LeaveRequest 的請求
    @Query("SELECT a FROM ApprovalStep a WHERE a.requestId = :requestId ORDER BY a.currentStep ASC")
    List<ApprovalStep> findApprovalStepByLeaveRequestId(@Param("requestId") Integer requestId);

    boolean existsByFlowIdIn(List<Integer> flowIdsToDelete);

	List<ApprovalStep> findByFlowIdIn(List<Integer> flowIdsToDelete); 
}


