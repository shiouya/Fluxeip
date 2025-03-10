package com.example.fluxeip.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fluxeip.model.Attendance;
import com.example.fluxeip.service.AttendanceService;

import tw.eeit1462.springmvcproject.exception.AttendanceNotFoundException;
import tw.eeit1462.springmvcproject.exception.AttendanceTodayNotFoundException;
import tw.eeit1462.springmvcproject.exception.EmployeeNotFoundException;

@RestController
@RequestMapping("/api/attendancelogs")
public class AttendanceLogsController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 取得當天的考勤紀錄（包含 Logs 和 Violations）
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayAttendance(@RequestParam int employeeId) {
        try {
            Attendance todayAttendance = attendanceService.getAttendanceWithDetails(employeeId, LocalDate.now());
            return ResponseEntity.ok(todayAttendance);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.badRequest().body("員工不存在: " + e.getMessage());
        } catch (AttendanceTodayNotFoundException e) {
            return ResponseEntity.badRequest().body("今日無考勤紀錄: " + e.getMessage());
        }
    }

    /**
     * 取得歷史考勤紀錄（特定日期、當月或當年）
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryAttendance(@RequestParam int employeeId, 
                                                  @RequestParam(required = false) String date) {
        try {
            LocalDate queryDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
            
            if (queryDate != null) {
                // 查詢指定日期的考勤
                Attendance attendance = attendanceService.getAttendanceByDate(employeeId, queryDate);
                return ResponseEntity.ok(attendance);
            } else {
                // 查詢當月的考勤
                LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                List<Attendance> monthlyAttendances = attendanceService.getAttendancesForMonth(employeeId, startOfMonth);

                // 查詢當年的考勤
                LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
                List<Attendance> yearlyAttendances = attendanceService.getAttendancesForYear(employeeId, startOfYear);

                // 包裝結果
                return ResponseEntity.ok(new AttendanceHistoryResponse(monthlyAttendances, yearlyAttendances));
            }
        } catch (AttendanceNotFoundException e) {
            return ResponseEntity.badRequest().body("查無此考勤紀錄: " + e.getMessage());
        }
    }

    /**
     * 用於封裝當月 & 當年的考勤資料
     */
    private static class AttendanceHistoryResponse {
        public List<Attendance> monthlyAttendances;
        public List<Attendance> yearlyAttendances;

        public AttendanceHistoryResponse(List<Attendance> monthlyAttendances, List<Attendance> yearlyAttendances) {
            this.monthlyAttendances = monthlyAttendances;
            this.yearlyAttendances = yearlyAttendances;
        }
    }
}
