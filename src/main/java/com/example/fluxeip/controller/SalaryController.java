package com.example.fluxeip.controller;

import java.math.BigDecimal;
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
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.SalaryBonus;
import com.example.fluxeip.model.SalaryDetail;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.service.EmployeeService;
import com.example.fluxeip.service.SalaryService;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryController {
	
	@Autowired
	private SalaryService salaryService;
	@Autowired
	private EmployeeRepository employeeRepository;
	
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
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "成功"));

		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	// 用員工搜尋明細
	@GetMapping("/detail/{id}")
	public ResponseEntity<?> findSalaryDetail(@PathVariable("id") Integer empId){
		try {
			List<SalaryDetailResponse> response = salaryService.findAllSalaryDetailByEmpId(empId);
			
			return ResponseEntity.ok(response);
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	// 搜尋全部明細
	@GetMapping("/detail")
	public ResponseEntity<?> findAllSalaryDetail(){
		try {
			List<SalaryDetailResponse> response = salaryService.findAllSalaryDetail();
			
			return ResponseEntity.ok(response);
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	//刪除明細
	@DeleteMapping("/detail/{id}")
	public ResponseEntity<?> deleteDetail(@PathVariable Integer id){

		boolean delete=salaryService.deleteDetailById(id);

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
	
	//遲到早退
	@GetMapping("/lateEarly")
	public Map<String, Integer> lateEarly(@RequestParam String yearMonth,@RequestParam Integer empId) {
		return salaryService.countMonthlyLateAndEarlyLeaveByEmpId(empId, yearMonth);
	}
	
	//請假時數
	@GetMapping("/leaveDays")
	public Double leaveDays(@RequestParam String yearMonth,@RequestParam Integer empId) {
		return salaryService.leaveDaysHours(empId, yearMonth);
	}
	
	//月總工時
	@GetMapping("/monthlyWorkHours")
	public BigDecimal monthlyWorkHours(@RequestParam String yearMonth,@RequestParam Integer empId) {
		return salaryService.countMonthlyWorkHours(empId, yearMonth);
	}
	
	//加班減班
	@GetMapping("/overtimeMinus")
	public Map<String, Integer> overtimeMinus(@RequestParam String yearMonth,@RequestParam Integer empId){
		return salaryService.overtimeAndMinus(empId, yearMonth);
	}
	//全部獎金 津貼
	@GetMapping("/bonus")
	public ResponseEntity<?> allBonus(){
		List<SalaryBonus> allBonus = salaryService.findAllBonus();
		
		return ResponseEntity.ok(allBonus);
	}
	//應得薪資
	@PostMapping("/earnedSalary")
	public Integer earnedSalary(@RequestBody SalaryDetailRequest detailRequest) {
		return salaryService.caculateEarnedSalary(detailRequest);
	}
	
	//全部員工
	@GetMapping("/allEmp")
	public List<Employee> findAllEmp(){
		return employeeRepository.findAll();
	}
	
	//新增bonus
	@PostMapping("/newBonus")
	public ResponseEntity<?> newBonus(@RequestParam Integer amount,@RequestParam String bonusType){
		try {
			salaryService.insertNewBonus(bonusType, amount);
			return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully");
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	//修改獎金津貼數字
	@PostMapping("/newBonus/{id}")
	public ResponseEntity<?> updateBonus(@PathVariable("id") Integer salaryBonusId,@RequestParam Integer amount){
		try {
			salaryService.updateBonus(salaryBonusId, amount);
			return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully");
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	//修改獎金津貼名稱
	@PutMapping("/newBonus/{id}")
	public ResponseEntity<?> updateBonusName(@PathVariable("id") Integer salaryBonusId,@RequestParam String bonusType){
		try {
			salaryService.updateBonusName(salaryBonusId, bonusType);
			return ResponseEntity.status(HttpStatus.CREATED).body("updated successfully");
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	//獎金津貼改為不可用
	@DeleteMapping("/newBonus/{id}")
	public ResponseEntity<?> deleteBonus(@PathVariable("id") Integer salaryBonusId){
		try {
			salaryService.deleteBonusById(salaryBonusId);
			return ResponseEntity.status(HttpStatus.CREATED).body("deleted successfully");
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
}
