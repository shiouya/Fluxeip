package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceViolations;



public interface AttendanceViolationsRepository extends JpaRepository<AttendanceViolations, Integer> {

	List<AttendanceViolations> findByAttendance(Attendance attendance);

	// 檢查某個考勤是否存在違規記錄
	@Query("SELECT COUNT(av) > 0 FROM AttendanceViolations av WHERE av.attendance = :attendance")
	boolean existsByAttendance(@Param("attendance") Attendance attendance);

}
