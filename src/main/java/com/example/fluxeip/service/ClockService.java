package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.model.AttendanceLogs;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.FieldWorkRecord;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.repository.AttendanceLogsRepository;
import com.example.fluxeip.repository.AttendanceRepository;
import com.example.fluxeip.repository.AttendanceViolationsRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.FieldWorkRecordRepository;
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
    @Autowired
    private FieldWorkRecordRepository fieldWorkRecordRepository;

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
        
        // 更新外勤時數
        if ("外出結束".equals(typeName)) {
            updateFieldWorkHours(attendance, employee, today);
        }

        // 更新出勤時數
        if ("下班".equals(typeName)) {
            updateAttendanceHours(attendance);
        }
        
        return exceptionMessage != null ? exceptionMessage : "打卡成功";
        }
        return exceptionMessage;
    }
    
    private void updateFieldWorkHours(Attendance attendance, Employee employee, LocalDate today) {
        if(attendance==null) {
        	return;
        }
        Integer attendanceId = attendance.getId();
        List<AttendanceLogs> startLogs = attendanceLogsRepository.findByAttendance_IdAndClockType_TypeName(attendanceId, "外出打卡");
        List<AttendanceLogs> endLogs = attendanceLogsRepository.findByAttendance_IdAndClockType_TypeName(attendanceId, "外出結束");

        System.out.println(startLogs.getFirst().getClockTime());
        System.out.println(endLogs.getFirst().getClockTime());
        if (startLogs.size() != endLogs.size()) {
            return; // 避免異常狀況
        }

        long totalFieldWorkMinutes = 0; 
        for (int i = 0; i < startLogs.size(); i++) {
            LocalDateTime start = startLogs.get(i).getClockTime();
            LocalDateTime end = endLogs.get(i).getClockTime();
            totalFieldWorkMinutes += Duration.between(start, end).toMinutes();
        }

        // 使用 RoundingMode.HALF_UP 確保四捨五入
        BigDecimal fieldWorkHours = BigDecimal.valueOf(totalFieldWorkMinutes)
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        attendance.setFieldWorkHours(fieldWorkHours);
        attendanceRepository.save(attendance);

        Optional<FieldWorkRecord> fieldWorkRecordOpt = fieldWorkRecordRepository.findByEmployee_EmployeeIdAndFieldWorkDate(employee.getEmployeeId(), today);

        if (fieldWorkRecordOpt.isPresent()) {
            FieldWorkRecord fieldWorkRecord = fieldWorkRecordOpt.get();
            fieldWorkRecord.setTotalHours(fieldWorkHours);
            fieldWorkRecordRepository.save(fieldWorkRecord); 
        } else if (fieldWorkHours.compareTo(BigDecimal.ZERO) > 0) {
            FieldWorkRecord newFieldWork = new FieldWorkRecord();
            newFieldWork.setEmployee(employee);
            newFieldWork.setFieldWorkDate(today);
            newFieldWork.setTotalHours(fieldWorkHours);
            newFieldWork.setLocation("未知");
            newFieldWork.setPurpose("未知");
            newFieldWork.setStatus(statusRepository.findByStatusNameAndStatusType("未填寫", "外勤表單狀態").get());
            fieldWorkRecordRepository.save(newFieldWork);
        }
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
                    
                    // 使用 BigDecimal 初始化數值
                    newAttendance.setTotalHours(BigDecimal.ZERO);
                    newAttendance.setRegularHours(BigDecimal.valueOf(8));
                    newAttendance.setOvertimeHours(BigDecimal.ZERO);
                    newAttendance.setFieldWorkHours(BigDecimal.ZERO);
                    
                    newAttendance.setHasViolation(false);
                    return attendanceRepository.save(newAttendance);
                });
    }


    private long calculateViolationMinutes(LocalDateTime now, ShiftType shiftType, String typeName) {
        LocalDateTime shiftStartTime = LocalDate.now().atTime(shiftType.getStartTime());
        LocalDateTime shiftEndTime = LocalDate.now().atTime(shiftType.getFinishTime());
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
        LocalDateTime shiftStartTime = LocalDate.now().atTime(shiftType.getStartTime());
        LocalDateTime shiftEndTime = LocalDate.now().atTime(shiftType.getFinishTime());

        switch (typeName) {
            case "上班":
                if (now.isBefore(shiftStartTime.minusHours(1))) return "非上班時間";
                if (hasClockedIn(attendance)) return "重複打卡";
                if (now.isAfter(shiftStartTime.plusMinutes(10))) return "遲到";
                break;

            case "下班":
            	if (!hasClockedIn(attendance)) return "非上班時間";
            	if (hasClockedOut(attendance)) return "重複打卡";
            	if (hasUnfinishedFieldWork(attendance)) return "缺外出結束"; // 需完成所有外出結束
                if (now.isBefore(shiftEndTime.minusMinutes(10))) return "早退";
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
        LocalDateTime now = LocalDateTime.now();
        
        long totalMinutes = Duration.between(attendance.getCreatedAt(), now).toMinutes();
        
        // 計算總工時並轉換為 BigDecimal
        BigDecimal totalHours = BigDecimal.valueOf(Math.max(0, (totalMinutes - 60) / 60.0))
                                          .setScale(2, RoundingMode.HALF_UP);
        attendance.setTotalHours(totalHours);

        // 計算加班時數
        if (totalHours.compareTo(attendance.getRegularHours()) > 0) {
            attendance.setOvertimeHours(totalHours.subtract(attendance.getRegularHours()));
        } else {
            attendance.setOvertimeHours(BigDecimal.ZERO);
        }

        // 檢查是否有違規
        attendance.setHasViolation(attendanceViolationsRepository.existsByAttendance(attendance));

        attendanceRepository.save(attendance);
    }

}

