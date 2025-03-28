package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeApprovalFlow;

public interface EmployeeApprovalFlowRepository extends JpaRepository<EmployeeApprovalFlow, Integer> {

	@Query("SELECT eaf FROM EmployeeApprovalFlow eaf WHERE eaf.employee.employeeId = :employeeId")
	List<EmployeeApprovalFlow> findByEmployeeId(@Param("employeeId") Integer employeeId);

	boolean existsByEmployeeAndApprovalFlow(Employee employee, ApprovalFlow approvalFlow);
}