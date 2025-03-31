package com.example.fluxeip.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceLogs;

public interface AttendanceLogsRepository extends JpaRepository<AttendanceLogs, Integer> {
	List<AttendanceLogs> findByEmployee_EmployeeId(int employeeId);

	List<AttendanceLogs> findByAttendance(Attendance attendance);

	List<AttendanceLogs> findByEmployee_EmployeeIdAndClockTimeBetween(int employeeId, LocalDateTime start,
			LocalDateTime end);

	// 檢查是否存在特定類型的打卡記錄
	@Query("SELECT COUNT(al) > 0 FROM AttendanceLogs al WHERE al.attendance = :attendance AND al.clockType.typeName = :typeName")
	boolean existsByAttendanceAndClockTypeName(@Param("attendance") Attendance attendance,
			@Param("typeName") String typeName);

	// 計算某類型打卡次數
	@Query("SELECT COUNT(al) FROM AttendanceLogs al WHERE al.attendance = :attendance AND al.clockType.typeName = :typeName")
	long countByAttendanceAndClockTypeName(@Param("attendance") Attendance attendance,
			@Param("typeName") String typeName);

	List<AttendanceLogs> findByAttendance_IdAndClockType_TypeName(Integer attendanceId, String string);

}
