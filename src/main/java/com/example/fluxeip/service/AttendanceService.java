package com.example.fluxeip.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.exception.AttendanceNotFoundException;
import com.example.fluxeip.exception.AttendanceTodayNotFoundException;
import com.example.fluxeip.exception.EmployeeNotFoundException;
import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.repository.AttendanceRepository;
import com.example.fluxeip.repository.AttendanceViolationsRepository;
import com.example.fluxeip.repository.EmployeeRepository;


@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceViolationsRepository attendanceViolationsRepository;

    public Attendance getAttendanceWithDetails(int employeeId, LocalDate date) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException("員工不存在"));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Attendance attendance = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee, startOfDay, endOfDay)
            .orElseThrow(() -> new AttendanceTodayNotFoundException("沒有找到今日出勤記錄"));

        // 獲取違規紀錄
        List<AttendanceViolations> violations = attendanceViolationsRepository.findByAttendance(attendance);
        attendance.setAttendanceViolations(violations);

        return attendance;
    }

    public List<Attendance> getAttendancesForMonth(int employeeId, LocalDate month) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException("員工不存在"));

        LocalDateTime startOfMonth = month.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = month.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        return attendanceRepository.findAllAttendancesByEmployeeAndCreatedAtBetween(employee, startOfMonth, endOfMonth);
    }

    public List<Attendance> getAttendancesForYear(int employeeId, LocalDate year) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException("員工不存在"));

        LocalDateTime startOfYear = year.withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfYear = year.plusYears(1).withDayOfYear(1).atStartOfDay();

        return attendanceRepository.findAllAttendancesByEmployeeAndCreatedAtBetween(employee, startOfYear, endOfYear);
    }
    // 新增的指定日期查詢方法
    public Attendance getAttendanceByDate(int employeeId, LocalDate date) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException("員工不存在"));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Attendance attendance = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee, startOfDay, endOfDay)
            .orElseThrow(() -> new AttendanceNotFoundException("沒有找到該日期的出勤記錄"));

        // 獲取違規紀錄
        List<AttendanceViolations> violations = attendanceViolationsRepository.findByAttendance(attendance);
        attendance.setAttendanceViolations(violations);

        return attendance;
    }
}
