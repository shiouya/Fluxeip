package com.example.fluxeip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.SalaryDefaultSetting;
import com.example.fluxeip.service.SalaryService;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryController {
	
	@Autowired
	private SalaryService salaryService;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> findSalarySettingByEmpId(@PathVariable("id") Integer empId) {
		SalaryDefaultSetting salary = salaryService.findSalarySettingByEmpid(empId);
		
		if (salary == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(salary);
	}
	
	@GetMapping
	public ResponseEntity<?> findAllSalarySetting(){
		List<SalaryDefaultSetting> allSalarySetting = salaryService.findAllSalarySetting();
		
		if (allSalarySetting == null||allSalarySetting.size()==0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("查無薪資設定");
		}
		return ResponseEntity.ok(allSalarySetting);
	}
	
	@GetMapping("/calculateHourly")
    public ResponseEntity<Map<String, Integer>> calculateHourly(@RequestParam Integer monthlySalary) {
        Integer hourlyWage = salaryService.caculateHourlyWage(monthlySalary);
        Map<String, Integer> response = new HashMap<>();
        response.put("hourlyWage", hourlyWage);
        return ResponseEntity.ok(response);
    }

	@PostMapping
	public ResponseEntity<?> insertSalarySetting(@RequestBody SalaryDefaultSetting salaryDefaultSetting){
		try {
			salaryService.settingDefaultSalary(salaryDefaultSetting);
			return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully");

		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
}
