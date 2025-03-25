package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.model.Status;


public interface TaskassignRepository extends JpaRepository<Taskassign, Integer> {

	List<Taskassign> findByWorkprogess(WorkProgess workprogess);
	
	List<Taskassign> findByAssign(Employee assign);
	
	List<Taskassign> findByAssignAndStatus(Employee assign, Status status);

}
