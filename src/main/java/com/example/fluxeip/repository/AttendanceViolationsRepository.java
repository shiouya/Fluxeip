package com.example.fluxeip.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Type;



public interface AttendanceViolationsRepository extends JpaRepository<AttendanceViolations, Integer> {

	List<AttendanceViolations> findByAttendance(Attendance attendance);

	// 檢查某個考勤是否存在違規記錄
	@Query("SELECT COUNT(av) > 0 FROM AttendanceViolations av WHERE av.attendance = :attendance")
	boolean existsByAttendance(@Param("attendance") Attendance attendance);

	
	@Query("SELECT a FROM AttendanceViolations a " +
		       "WHERE a.employee.employeeId = :employeeId " +
		       "AND a.violationType.typeName = :violationTypeName " +
		       "AND a.createdAt BETWEEN :startDate AND :endDate")
		List<AttendanceViolations> findViolationsByEmployeeAndTypeAndMonth(
		        @Param("employeeId") Integer employeeId,
		        @Param("violationTypeName") String violationTypeName,
		        @Param("startDate") LocalDateTime startDate,
		        @Param("endDate") LocalDateTime endDate);

	Optional<AttendanceViolations> findByViolationType(Type type);
}
