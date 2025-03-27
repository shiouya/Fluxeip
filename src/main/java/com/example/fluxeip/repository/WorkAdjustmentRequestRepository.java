package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.WorkAdjustmentRequest;

public interface WorkAdjustmentRequestRepository extends JpaRepository<WorkAdjustmentRequest, Integer> {

	List<WorkAdjustmentRequest> findByEmployee_EmployeeId(Integer employeeId);
 
	@Query("SELECT COUNT(w) > 0 FROM WorkAdjustmentRequest w WHERE w.id IN :ids AND w.status.statusId NOT IN :statusIds AND w.employee.id = :employeeId")
	boolean existsByRequestIdsAndStatusNotInAndEmployeeId(@Param("ids") List<Integer> ids, @Param("statusIds") List<Integer> statusIds, @Param("employeeId") Integer employeeId);
	
}


