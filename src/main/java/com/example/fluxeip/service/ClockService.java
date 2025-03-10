package com.example.fluxeip.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceLogs;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.repository.AttendanceLogsRepository;
import com.example.fluxeip.repository.AttendanceRepository;
import com.example.fluxeip.repository.AttendanceViolationsRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.ScheduleRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;

@Service
@Transactional
public class ClockService {

    @Autowired
    private AttendanceLogsRepository attendanceLogsRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AttendanceViolationsRepository attendanceViolationsRepository;

    public String clockIn(int employeeId) {
        return handleClockEvent(employeeId, "上班");
    }

    public String clockOut(int employeeId) {
        return handleClockEvent(employeeId, "下班");
    }

    public String startFieldWork(int employeeId) {
        return handleClockEvent(employeeId, "外出打卡");
    }

    public String endFieldWork(int employeeId) {
        return handleClockEvent(employeeId, "外出結束");
    }

    private String handleClockEvent(int employeeId, String typeName) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return "員工不存在";
        }
        Employee employee = employeeOpt.get();
        
        Optional<Type> typeOpt = typeRepository.findByTypeName(typeName);
        if (typeOpt.isEmpty()) {
            return "無效的打卡類型";
        }
        Type clockType = typeOpt.get();
        
        LocalDate today = LocalDate.now();
        Optional<ShiftType> shiftOpt = scheduleRepository.findShiftTypeByEmployeeIdAndDate(employeeId, today);
        if (shiftOpt.isEmpty()) {
            return "找不到今日班別";
        }
        ShiftType shiftType = shiftOpt.get();
        
        Status status = statusRepository.findByStatusName("在職").orElse(null);
        if (status == null) {
            return "無效的出勤狀態";
        }
        
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = getOrCreateAttendance(employee, shiftType, status);
        
        // 檢查異常
        String exceptionMessage = checkForExceptions(now, shiftType, typeName, attendance);
        if (exceptionMessage != null) {
            long violationMinutes = calculateViolationMinutes(now, shiftType, typeName);
            recordViolation(employee, attendance, exceptionMessage, violationMinutes);
        }
        if(exceptionMessage=="遲到"||exceptionMessage=="早退"||exceptionMessage==null) {
        // 記錄打卡
        AttendanceLogs log = new AttendanceLogs();
        log.setAttendance(attendance);
        log.setEmployee(employee);
        log.setClockType(clockType);
        log.setClockTime(now);
        attendanceLogsRepository.save(log);
        
        // 更新出勤時數
        if ("下班".equals(typeName)) {
            updateAttendanceHours(attendance);
        }
        
        return exceptionMessage != null ? exceptionMessage : "打卡成功";
        }
        return exceptionMessage;
    }


    private Attendance getOrCreateAttendance(Employee employee, ShiftType shiftType, Status status) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        return attendanceRepository.findByEmployeeAndCreatedAtBetween(employee, todayStart, todayEnd)
                .orElseGet(() -> {
                    Attendance newAttendance = new Attendance();
                    newAttendance.setEmployee(employee);
                    newAttendance.setShiftType(shiftType);
                    newAttendance.setStatus(status);
                    newAttendance.setTotalHours(0);
                    newAttendance.setRegularHours(8);
                    newAttendance.setOvertimeHours(0);
                    newAttendance.setFieldWorkHours(0);
                    newAttendance.setHasViolation(false);
                    return attendanceRepository.save(newAttendance);
                });
    }

    private long calculateViolationMinutes(LocalDateTime now, ShiftType shiftType, String typeName) {
        LocalDateTime shiftStartTime = LocalDate.now().atTime(shiftType.getStartTime().toLocalTime());
        LocalDateTime shiftEndTime = LocalDate.now().atTime(shiftType.getFinishTime().toLocalTime());
        return switch (typeName) {
            case "上班" -> Duration.between(shiftStartTime.plusMinutes(10), now).toMinutes();
            case "下班" -> Duration.between(now, shiftEndTime.minusMinutes(10)).toMinutes();
            default -> 0;
        };
    }

    private void recordViolation(Employee employee, Attendance attendance, String exceptionMessage, long violationMinutes) {
        typeRepository.findByTypeName(exceptionMessage).ifPresent(violationType -> {
            AttendanceViolations violation = new AttendanceViolations();
            violation.setEmployee(employee);
            violation.setAttendance(attendance);
            violation.setViolationType(violationType);
            violation.setViolationMinutes((int) violationMinutes);
            violation.setCreatedAt(LocalDateTime.now());
            attendanceViolationsRepository.save(violation);
        });
    }

    private String checkForExceptions(LocalDateTime now, ShiftType shiftType, String typeName, Attendance attendance) {
        LocalDateTime shiftStartTime = LocalDate.now().atTime(shiftType.getStartTime().toLocalTime());
        LocalDateTime shiftEndTime = LocalDate.now().atTime(shiftType.getFinishTime().toLocalTime());

        switch (typeName) {
            case "上班":
                if (now.isBefore(shiftStartTime.minusHours(1))) return "非上班時間";
                if (now.isAfter(shiftStartTime.plusMinutes(10))) return "遲到";
                break;

            case "下班":
            	if (!hasClockedIn(attendance)) return "非上班時間";
                if (now.isBefore(shiftEndTime.minusMinutes(10))) return "早退";
                if (hasUnfinishedFieldWork(attendance)) return "缺外出結束"; // 需完成所有外出結束
                break;

            case "外出打卡":
                if (!hasClockedIn(attendance) || hasClockedOut(attendance)) return "非外出打卡時間";
                if (hasUnfinishedFieldWork(attendance)) return "缺外出結束"; // 不能在未結束的外出上再打新的外出
                break;

            case "外出結束":
            	if (!hasClockedIn(attendance) || hasClockedOut(attendance)) return "非外出打卡時間";
                if (!hasUnfinishedFieldWork(attendance)) return "缺外出打卡"; // 不能沒有「外出打卡」就直接按「外出結束」
                break;
        }
        return null;
    }

 // 是否有「上班」記錄
    private boolean hasClockedIn(Attendance attendance) {
        return attendanceLogsRepository.existsByAttendanceAndClockTypeName(attendance, "上班");
    }

    // 是否有「下班」記錄
    private boolean hasClockedOut(Attendance attendance) {
        return attendanceLogsRepository.existsByAttendanceAndClockTypeName(attendance, "下班");
    }

    // 檢查是否有尚未結束的「外出」
    private boolean hasUnfinishedFieldWork(Attendance attendance) {
        long fieldWorkStartCount = attendanceLogsRepository.countByAttendanceAndClockTypeName(attendance, "外出打卡");
        long fieldWorkEndCount = attendanceLogsRepository.countByAttendanceAndClockTypeName(attendance, "外出結束");
        return fieldWorkStartCount > fieldWorkEndCount; // 若外出次數 > 外出結束次數，代表仍有未結束的外出
    }


    private void updateAttendanceHours(Attendance attendance) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        
        long totalMinutes = Duration.between(attendance.getCreatedAt(), now).toMinutes();
        attendance.setTotalHours((int) (totalMinutes / 60));
        
        if (attendance.getTotalHours() > attendance.getRegularHours()) {
            attendance.setOvertimeHours(attendance.getTotalHours() - attendance.getRegularHours());
        }
        
        attendance.setHasViolation(attendanceViolationsRepository.existsByAttendance(attendance));
        attendanceRepository.save(attendance);
    }
}

