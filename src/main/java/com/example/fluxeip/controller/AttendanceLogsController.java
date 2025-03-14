package com.example.fluxeip.controller;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fluxeip.dto.AttendanceDTO;
import com.example.fluxeip.service.AttendanceService;
import com.example.fluxeip.jwt.JsonWebTokenUtility;

@RestController
@RequestMapping("/api/attendancelogs")
public class AttendanceLogsController {

    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    /**
     * 取得當天的考勤紀錄（包含 Logs 和 Violations）
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayAttendance(@RequestHeader("Authorization") String authorization) {
    	int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return ResponseEntity.badRequest().body("無效的token");
        }
        AttendanceDTO todayAttendance = attendanceService.getAttendanceWithDetails(employeeId, LocalDate.now());

        if (todayAttendance == null) {
            return ResponseEntity.badRequest().body("今日無考勤紀錄或員工不存在");
        }

        return ResponseEntity.ok(todayAttendance);
    }

    /**
     * 取得歷史考勤紀錄（特定日期、當月或當年）
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryAttendance(@RequestHeader("Authorization") String authorization, 
                                                  @RequestParam(required = false) String date) {
    	int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return ResponseEntity.badRequest().body("無效的token");
        }
        if (date != null && !date.isEmpty()) {
            // 查詢指定日期的考勤
            AttendanceDTO attendance = attendanceService.getAttendanceByDate(employeeId, LocalDate.parse(date));
            if (attendance == null) {
                return ResponseEntity.badRequest().body("指定日期無考勤紀錄或員工不存在");
            }
            return ResponseEntity.ok(attendance);
        } else {
            // 查詢當月的考勤
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            List<AttendanceDTO> monthlyAttendances = attendanceService.getAttendancesForMonth(employeeId, startOfMonth);

            // 查詢當年的考勤
            LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
            List<AttendanceDTO> yearlyAttendances = attendanceService.getAttendancesForYear(employeeId, startOfYear);

            // 如果員工不存在，回傳錯誤
            if (monthlyAttendances == null || yearlyAttendances == null) {
                return ResponseEntity.badRequest().body("員工不存在");
            }

            return ResponseEntity.ok(new AttendanceHistoryResponse(monthlyAttendances, yearlyAttendances));
        }
    }

    private int extractEmployeeIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return -1; 
        }

        String token = authorization.substring(7);
        String userJsonString = jsonWebTokenUtility.validateToken(token); 

        if (userJsonString == null || userJsonString.isEmpty()) {
            return -1; 
        }

        try {
            JSONObject userJson = new JSONObject(userJsonString); 
            return userJson.getInt("id"); 
        } catch (Exception e) {
            e.printStackTrace(); 
            return -1;
        }
    }

    /**
     * 用於封裝當月 & 當年的考勤資料
     */
    private static class AttendanceHistoryResponse {
        public List<AttendanceDTO> monthlyAttendances;
        public List<AttendanceDTO> yearlyAttendances;

        public AttendanceHistoryResponse(List<AttendanceDTO> monthlyAttendances, List<AttendanceDTO> yearlyAttendances) {
            this.monthlyAttendances = monthlyAttendances;
            this.yearlyAttendances = yearlyAttendances;
        }
    }
}
