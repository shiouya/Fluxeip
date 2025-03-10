package com.example.fluxeip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.service.EmployeeService;

@RestController
public class EmployeeController {
	
	@Autowired
    private EmployeeService employeeService;
	
	@CrossOrigin
	@PostMapping("/employee/find")
    public Page<Employee> getEmployees(
            @RequestParam int page, // 預設頁碼是 0
            @RequestParam int size // 預設每頁 10 條數據
//            @RequestParam(required = false) String department, // 可選的部門查詢條件
//            @RequestParam(required = false) String position // 可選的職位查詢條件
    ) {
        return employeeService.getEmployees(page, size);
    }

}
