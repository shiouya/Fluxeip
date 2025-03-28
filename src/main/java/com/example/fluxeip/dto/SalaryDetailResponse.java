package com.example.fluxeip.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.fluxeip.model.SalaryBonus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryDetailResponse {

	private Integer salaryDetailId;
	private Integer employeeId;
	private String employeeName;
	private String department;	
	private String yearMonth;
	private BigDecimal monthlyRegularHours;
	private BigDecimal overtimeHours;
	private Integer lateHours;
	private Integer earlyLeaveHours;
	private Integer leaveDays;
	private Integer healthInsurance;
	private Integer laborInsurance;
	private List<SalaryBonus> bonuses;
	private Integer yearEnd;
	private Integer earnedSalary;
	private Map<String,Double> leaveDaysHoursByType;
}
