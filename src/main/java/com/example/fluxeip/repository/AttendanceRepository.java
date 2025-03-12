package com.example.fluxeip.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
	
//	@EntityGraph(attributePaths = {"attendanceLogs"})  // 只先抓 logs
//    Optional<Attendance> findByEmployeeAndCreatedAtBetween(Employee employee, LocalDateTime start, LocalDateTime end);

	 @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND a.createdAt BETWEEN :startTime AND :endTime")
		Optional<Attendance> findByEmployeeAndCreatedAtBetween(@Param("employee") Employee employee,
	                                                           @Param("startTime") LocalDateTime startTime,
	                                                           @Param("endTime") LocalDateTime endTime);
	 
	@Query("SELECT a FROM Attendance a LEFT JOIN FETCH a.attendanceLogs LEFT JOIN FETCH a.attendanceViolations WHERE a.employee.id = :employeeId")
	List<Attendance> findByEmployeeIdWithDetails(@Param("employeeId") int employeeId);
	
    List<Attendance> findAllAttendancesByEmployeeAndCreatedAtBetween(Employee employee, LocalDateTime start, LocalDateTime end);
    
    

}
