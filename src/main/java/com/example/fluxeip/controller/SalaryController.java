package com.example.fluxeip.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.SalaryDefaultSetting;
import com.example.fluxeip.dto.SalaryDetailRequest;
import com.example.fluxeip.dto.SalaryDetailResponse;
import com.example.fluxeip.model.SalaryDetail;
import com.example.fluxeip.service.SalaryService;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryController {
	
	@Autowired
	private SalaryService salaryService;
	
	//薪資設定相關
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
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateSalarySetting(@PathVariable("id") Integer empId,@RequestBody SalaryDefaultSetting salaryDefaultSetting){
		
		try {
			salaryService.updateSalarySetting(empId, salaryDefaultSetting);
			return ResponseEntity.status(HttpStatus.CREATED).body("Updated successfully");

		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteSalarySetting(@PathVariable("id") Integer empId){
		
		boolean delete = salaryService.deleteSalarySettingByEmpId(empId);
		
		Map<String, String> response = new HashMap<>();
		if (delete) {
			response.put("message", "success");
			response.put("success", "true");

			return ResponseEntity.ok(response); // 200 OK，帶回訊息
		} else {
			response.put("message", "false");
			response.put("success", "false");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
		}
	}
	
	//薪資結算相關
	
	@PostMapping("/detail")
	public ResponseEntity<?> createSalaryDetail(@RequestBody SalaryDetailRequest detailRequest){
		
		try {
			salaryService.monthlySalaryCaculate(detailRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully");

		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@GetMapping("/detail/{id}")
	public ResponseEntity<?> findSalaryDetail(@PathVariable("id") Integer empId){
		try {
			List<SalaryDetailResponse> response = salaryService.findSalaryDetailByEmpId(empId);
			
			return ResponseEntity.ok(response);
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	//勞健保
	@GetMapping("/insurance")
	public Map<String, Integer> insurance(@RequestParam Integer salary){
		return salaryService.laborInsuranceAndHealthInsurance(salary);
	}
	
	//年終
	@GetMapping("/yearEnd/{id}")
	public Integer yearEnd(@PathVariable("id") Integer empId, @RequestParam Integer month){
		SalaryDefaultSetting salarySetting = salaryService.findSalarySettingByEmpid(empId);
		Integer monthlySalary = salarySetting.getMonthlySalary();
		Integer hourlyWage = salarySetting.getHourlyWage();
		
		Integer yearEnd=0;
		

		if(monthlySalary.equals(0)) {
			yearEnd+=month*hourlyWage*200;
		}else {
			yearEnd+=month*monthlySalary;
		}
		

		return yearEnd;
	}
}
