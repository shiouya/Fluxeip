package com.example.fluxeip.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.ExpenseRequest;

public interface ExpenseRequestRepository extends JpaRepository<ExpenseRequest, Integer> {

	List<ExpenseRequest> findByEmployee_EmployeeId(Integer employeeId);
	
}


