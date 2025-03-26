package com.example.fluxeip.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.WorkAdjustmentRequest;

public interface WorkAdjustmentRequestRepository extends JpaRepository<WorkAdjustmentRequest, Integer> {

	List<WorkAdjustmentRequest> findByEmployee_EmployeeId(Integer employeeId);
	
	
	@Query("SELECT w FROM WorkAdjustmentRequest w " +
		       "WHERE w.employee.employeeId = :employeeId " +
		       "AND w.adjustmentType.typeName = :adjustmentType " +
		       "AND w.adjustmentDate BETWEEN :startDate AND :endDate")
		List<WorkAdjustmentRequest> findWorkAdjustmentRequestByEmployeeAndTypeAndMonth(
		        @Param("employeeId") Integer employeeId,
		        @Param("adjustmentType") String adjustmentType,
		        @Param("startDate") LocalDateTime startDate,
		        @Param("endDate") LocalDateTime endDate);
}


