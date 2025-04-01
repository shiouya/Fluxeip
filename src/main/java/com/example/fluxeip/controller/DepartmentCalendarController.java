package com.example.fluxeip.controller;

import com.example.fluxeip.dto.DepartmentCalendarRequestDTO;
import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.DepartmentCalendar;
import com.example.fluxeip.service.DepartmentCalendarService;
import com.example.fluxeip.service.EmployeeService;  // 假設你有一個 EmployeeService 用來獲取部門資訊
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")  // 根據 API 路徑設定
public class DepartmentCalendarController {

    private final DepartmentCalendarService departmentCalendarService;
    private final EmployeeService employeeService;  // 注入 EmployeeService 用來獲取部門

    public DepartmentCalendarController(DepartmentCalendarService departmentCalendarService, EmployeeService employeeService) {
        this.departmentCalendarService = departmentCalendarService;
        this.employeeService = employeeService;
    }

    // ✅ 根據員工 ID 獲取部門 ID
    @GetMapping("/employee/{empId}/department")
    public ResponseEntity<Department> getDepartmentByEmployeeId(@PathVariable Integer empId) {
        Department department = employeeService.getDepartmentByEmployeeId(empId);  // 假設這是 EmployeeService 的方法
        if (department != null) {
            return ResponseEntity.ok(department);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    // ✅ 取得指定部門的所有行事曆事件
    @GetMapping("/calendar/department/{departmentId}")
    public ResponseEntity<List<DepartmentCalendar>> getEventsByDepartment(@PathVariable Integer departmentId) {
        List<DepartmentCalendar> events = departmentCalendarService.getEventsByDepartment(departmentId);
        return ResponseEntity.ok(events);
    }

    // ✅ 新增部門行事曆事件
    @PostMapping("/calendar/department")
    public ResponseEntity<?> createEvent(@RequestBody DepartmentCalendarRequestDTO requestDTO) {
        if (!requestDTO.isValid()) {
            return ResponseEntity.badRequest().body(requestDTO.getValidationErrorMessage());
        }

        DepartmentCalendar createdEvent = departmentCalendarService.createEvent(requestDTO);
        return ResponseEntity.ok(createdEvent);
    }

    // ✅ 刪除部門行事曆事件
    @DeleteMapping("/calendar/department/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Integer id) {
        boolean isDeleted = departmentCalendarService.deleteEvent(id);
        if (isDeleted) {
            return ResponseEntity.ok("事件刪除成功");
        } else {
            return ResponseEntity.badRequest().body("事件未找到");
        }
    }
}
