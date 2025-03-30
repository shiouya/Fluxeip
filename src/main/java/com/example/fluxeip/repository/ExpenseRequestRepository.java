package com.example.fluxeip.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.ExpenseRequest;

public interface ExpenseRequestRepository extends JpaRepository<ExpenseRequest, Integer> {

	List<ExpenseRequest> findByEmployee_EmployeeId(Integer employeeId);

	@Query("SELECT COUNT(e) > 0 FROM ExpenseRequest e WHERE e.id IN :ids AND e.status.statusId NOT IN :statusIds AND e.employee.id = :employeeId")
	boolean existsByRequestIdsAndStatusNotInAndEmployeeId(@Param("ids") List<Integer> ids, @Param("statusIds") List<Integer> statusIds, @Param("employeeId") Integer employeeId);

	@Query("SELECT COUNT(e) > 0 FROM ExpenseRequest e WHERE e.id IN :ids AND e.status.statusId NOT IN :statusIds")
	boolean existsByRequestIdsAndStatusNotIn(@Param("ids") List<Integer> ids, @Param("statusIds") List<Integer> statusIds);
	
}


