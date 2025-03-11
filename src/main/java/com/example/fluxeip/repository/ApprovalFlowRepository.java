package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.ApprovalFlow;

public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Integer> {
	
    List<ApprovalFlow> findByRequestTypeIdOrderByStepOrderAsc(Integer requestTypeId);
    
    
}


