package com.example.fluxeip.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.SalaryDefaultSetting;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.SalarySetting;
import com.example.fluxeip.repository.SalaryBonusRepository;
import com.example.fluxeip.repository.SalaryDetailRepository;
import com.example.fluxeip.repository.SalarySettingRepository;

@Service
public class SalaryService {

	@Autowired
	private SalaryBonusRepository bonusRepository;

	@Autowired
	private SalaryDetailRepository detailRepository;

	@Autowired
	private SalarySettingRepository settingRepository;

	@Autowired
	private EmployeeService employeeService;

	private static final int legalMinimumWage = 190; // 最低工資 190 元/時

	public SalaryDefaultSetting findSalarySettingByEmpid(Integer empId) {
		
		Employee employee = employeeService.find(empId);
		return changeToResponse(settingRepository.findByEmployee(employee));
	}
	
	public List<SalaryDefaultSetting> findAllSalarySetting(){
		List<SalarySetting> all = settingRepository.findAll();
		ArrayList<SalaryDefaultSetting> list = new ArrayList<SalaryDefaultSetting>();
		
		for(SalarySetting salary:all) {
			list.add(changeToResponse(salary));
		}
		
		return list;
	}
	
	@Transactional
	public void settingDefaultSalary(SalaryDefaultSetting setting) {

		SalarySetting salary = salaryDefaultSettingRequsetToObject(setting);

		settingRepository.save(salary);
	}

	public Integer caculateHourlyWage(Integer monthlySalary) {

		int hourlyWage = Math.round(Math.round(monthlySalary / 30.0f) / 8.0f);

		if (hourlyWage < legalMinimumWage) {
			return legalMinimumWage;
		}
		return hourlyWage;
	}

	private SalarySetting salaryDefaultSettingRequsetToObject(SalaryDefaultSetting setting) {

		SalarySetting salarySetting = new SalarySetting();

		Employee employee = employeeService.find(setting.getEmployeeID());

		salarySetting.setEmployee(employee);
		salarySetting.setHourlyWage(setting.getHourlyWage());
		salarySetting.setMonthlySalary(setting.getMonthlySalary());
		salarySetting.setSalaryId(setting.getSalaryId());

		return salarySetting;
	}
	
	private SalaryDefaultSetting changeToResponse(SalarySetting salarySetting) {
		SalaryDefaultSetting setting = new SalaryDefaultSetting();
		
		setting.setEmployeeID(salarySetting.getEmployee().getEmployeeId());
		setting.setHourlyWage(salarySetting.getHourlyWage());
		setting.setMonthlySalary(salarySetting.getMonthlySalary());
		setting.setSalaryId(salarySetting.getSalaryId());
		
		return setting;
	}
}
