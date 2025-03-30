package com.example.fluxeip.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.MissingPunchRequest;

public interface MissingPunchRequestRepository extends JpaRepository<MissingPunchRequest, Integer> {

	List<MissingPunchRequest> findByEmployee_EmployeeId(Integer employeeId);

	@Query("SELECT COUNT(m) > 0 FROM MissingPunchRequest m WHERE m.id IN :ids AND m.status.statusId NOT IN :statusIds AND m.employee.id = :employeeId")
	boolean existsByRequestIdsAndStatusNotInAndEmployeeId(@Param("ids") List<Integer> ids, @Param("statusIds") List<Integer> statusIds, @Param("employeeId") Integer employeeId);

	@Query("SELECT COUNT(m) > 0 FROM MissingPunchRequest m WHERE m.id IN :ids AND m.status.statusId NOT IN :statusIds")
	boolean existsByRequestIdsAndStatusNotIn(@Param("ids") List<Integer> ids, @Param("statusIds") List<Integer> statusIds);
	
}


