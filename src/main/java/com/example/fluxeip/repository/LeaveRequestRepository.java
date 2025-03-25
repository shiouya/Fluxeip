package com.example.fluxeip.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.LeaveRequest;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
	
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.employeeId = :employeeId")
	List<LeaveRequest> findByEmployeeId(@Param("employeeId") Integer employeeId);
	
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.employeeId = :empid " +
	           "AND lr.status.statusName = :status " +
	           "AND lr.startDatetime BETWEEN :startDate AND :endDate")
	    List<LeaveRequest> findByEmpidAndStatusAndDateRange(@Param("empid") Integer empid,
	                                                         @Param("status") String status,
	                                                         @Param("startDate") LocalDateTime startOfMonth,
	                                                         @Param("endDate") LocalDateTime endOfMonth);
}
