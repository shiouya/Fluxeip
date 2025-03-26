package com.example.fluxeip.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.MissingPunchRequest;

public interface MissingPunchRequestRepository extends JpaRepository<MissingPunchRequest, Integer> {

	List<MissingPunchRequest> findByEmployee_EmployeeId(Integer employeeId);
	
}


