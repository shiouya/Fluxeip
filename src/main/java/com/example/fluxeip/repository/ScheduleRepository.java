package com.example.fluxeip.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Schedule;
import com.example.fluxeip.model.ShiftType;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT s.shiftType FROM Schedule s WHERE s.employee.employeeId = :employeeId AND s.scheduleDate = :date")
    Optional<ShiftType> findShiftTypeByEmployeeIdAndDate(@Param("employeeId") int employeeId, @Param("date") LocalDate date);
    
    @Query("SELECT s FROM Schedule s WHERE s.employee.employeeId = :employeeId AND s.scheduleDate = :date")
    List<Schedule> findScheduleByEmployeeIdAndDate(@Param("employeeId") int employeeId, @Param("date") LocalDate date);
    
    List<Schedule> findByEmployeeEmployeeIdAndScheduleDateBetween(int employeeId, LocalDate startDate, LocalDate endDate);

    List<Schedule> findByEmployeeEmployeeId(int employeeId);
    
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.employee.employeeId = :employeeId AND s.scheduleDate = :date")
    long countByEmployeeAndDate(@Param("employeeId") Integer employeeId, @Param("date") LocalDate date);

}
