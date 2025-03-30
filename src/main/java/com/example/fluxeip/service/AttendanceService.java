package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.AttendanceDTO;
import com.example.fluxeip.dto.AttendanceLogDTO;
import com.example.fluxeip.dto.AttendanceViolationDTO;
import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceLogs;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.repository.AttendanceLogsRepository;
import com.example.fluxeip.repository.AttendanceRepository;
import com.example.fluxeip.repository.AttendanceViolationsRepository;
import com.example.fluxeip.repository.EmployeeRepository;

@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private AttendanceLogsRepository attendanceLogsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceViolationsRepository attendanceViolationsRepository;
    
    public AttendanceDTO getAttendanceWithDetails(int employeeId, LocalDate date) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return null; // 員工不存在
        }

        Employee employee = employeeOpt.get();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee, startOfDay, endOfDay);
        if (attendanceOpt.isEmpty()) {
            return null; // 當日無考勤記錄
        }
        
        Attendance attendance = attendanceOpt.get();
        List<AttendanceViolations> violations = attendanceViolationsRepository.findByAttendance(attendance);
        List<AttendanceLogs> logs = attendanceLogsRepository.findByAttendance(attendance);

        // 轉換成 DTO
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setTotalHours(attendance.getTotalHours());
        attendanceDTO.setRegularHours(attendance.getRegularHours());
        attendanceDTO.setOvertimeHours(attendance.getOvertimeHours());
        attendanceDTO.setFieldWorkHours(attendance.getFieldWorkHours());
        attendanceDTO.setHasViolation(attendance.isHasViolation());
        
        List<AttendanceLogDTO> logsDTOs = new ArrayList<>();
        for (AttendanceLogs log : logs) {
            AttendanceLogDTO attendanceLogDTO = new AttendanceLogDTO();
            attendanceLogDTO.setClockTime(log.getClockTime());
            attendanceLogDTO.setClockType(log.getClockType().getTypeName());
            logsDTOs.add(attendanceLogDTO);
        }

        List<AttendanceViolationDTO> violationDTOs = new ArrayList<>();
        for (AttendanceViolations violation : violations) {
            AttendanceViolationDTO violationDTO = new AttendanceViolationDTO();
            violationDTO.setViolationType(violation.getViolationType().getTypeName());
            violationDTO.setViolationMinutes(violation.getViolationMinutes());
            violationDTO.setCreatedAt(violation.getCreatedAt());
            violationDTOs.add(violationDTO);
        }
        
        attendanceDTO.setAttendanceLogs(logsDTOs);
        attendanceDTO.setAttendanceViolations(violationDTOs);

        return attendanceDTO;
    }

    public List<AttendanceDTO> getAttendancesForMonth(int employeeId, LocalDate month) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return null; // 員工不存在
        }

        Employee employee = employeeOpt.get();
        LocalDateTime startOfMonth = month.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = month.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        List<Attendance> attendances = attendanceRepository.findAllAttendancesByEmployeeAndCreatedAtBetween(employee, startOfMonth, endOfMonth);

        List<AttendanceDTO> attendanceDTOs = new ArrayList<>();
        for (Attendance attendance : attendances) {
            AttendanceDTO attendanceDTO = new AttendanceDTO();
            attendanceDTO.setTotalHours(attendance.getTotalHours());
            attendanceDTO.setRegularHours(attendance.getRegularHours());
            attendanceDTO.setOvertimeHours(attendance.getOvertimeHours());
            attendanceDTO.setFieldWorkHours(attendance.getFieldWorkHours());
            attendanceDTO.setHasViolation(attendance.isHasViolation());

            attendanceDTOs.add(attendanceDTO);
        }

        return attendanceDTOs;
    }

    public List<AttendanceDTO> getAttendancesForYear(int employeeId, LocalDate year) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return null; // 員工不存在
        }

        Employee employee = employeeOpt.get();
        LocalDateTime startOfYear = year.withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfYear = year.plusYears(1).withDayOfYear(1).atStartOfDay();

        List<Attendance> attendances = attendanceRepository.findAllAttendancesByEmployeeAndCreatedAtBetween(employee, startOfYear, endOfYear);

        List<AttendanceDTO> attendanceDTOs = new ArrayList<>();
        for (Attendance attendance : attendances) {
            AttendanceDTO attendanceDTO = new AttendanceDTO();
            attendanceDTO.setTotalHours(attendance.getTotalHours());
            attendanceDTO.setRegularHours(attendance.getRegularHours());
            attendanceDTO.setOvertimeHours(attendance.getOvertimeHours());
            attendanceDTO.setFieldWorkHours(attendance.getFieldWorkHours());
            attendanceDTO.setHasViolation(attendance.isHasViolation());

            attendanceDTOs.add(attendanceDTO);
        }

        return attendanceDTOs;
    }

    public AttendanceDTO getAttendanceByDate(int employeeId, LocalDate date) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return null; // 員工不存在
        }

        Employee employee = employeeOpt.get();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeAndCreatedAtBetween(employee, startOfDay, endOfDay);
        if (attendanceOpt.isEmpty()) {
            return null; // 指定日期無考勤記錄
        }

        Attendance attendance = attendanceOpt.get();
        List<AttendanceViolations> violations = attendanceViolationsRepository.findByAttendance(attendance);
        List<AttendanceLogs> logs = attendanceLogsRepository.findByAttendance(attendance);
        
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setTotalHours(attendance.getTotalHours());
        attendanceDTO.setRegularHours(attendance.getRegularHours());
        attendanceDTO.setOvertimeHours(attendance.getOvertimeHours());
        attendanceDTO.setFieldWorkHours(attendance.getFieldWorkHours());
        attendanceDTO.setHasViolation(attendance.isHasViolation());
        
        List<AttendanceLogDTO> logsDTOs = new ArrayList<>();
        for (AttendanceLogs log : logs) {
            AttendanceLogDTO attendanceLogDTO = new AttendanceLogDTO();
            attendanceLogDTO.setClockTime(log.getClockTime());
            attendanceLogDTO.setClockType(log.getClockType().getTypeName());
            logsDTOs.add(attendanceLogDTO);
        }

        List<AttendanceViolationDTO> violationDTOs = new ArrayList<>();
        for (AttendanceViolations violation : violations) {
            AttendanceViolationDTO violationDTO = new AttendanceViolationDTO();
            violationDTO.setViolationType(violation.getViolationType().getTypeName());
            violationDTO.setViolationMinutes(violation.getViolationMinutes());
            violationDTO.setCreatedAt(violation.getCreatedAt());
            violationDTOs.add(violationDTO);
        }
        
        attendanceDTO.setAttendanceLogs(logsDTOs);
        attendanceDTO.setAttendanceViolations(violationDTOs);

        return attendanceDTO;
    }
}
