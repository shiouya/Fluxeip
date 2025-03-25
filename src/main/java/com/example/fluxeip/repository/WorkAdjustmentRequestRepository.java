package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.WorkAdjustmentRequest;

public interface WorkAdjustmentRequestRepository extends JpaRepository<WorkAdjustmentRequest, Integer> {

	List<WorkAdjustmentRequest> findByEmployee_EmployeeId(Integer employeeId);
	
}


