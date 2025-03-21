package com.example.fluxeip.dto;


import java.math.BigDecimal;
import java.util.List;

import com.example.fluxeip.model.SalaryBonus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryDetailRequest {

	private int salaryDetailId;
	private int employeeId;
	private String yearMonth;
	private BigDecimal monthlyRegularHours;
	private BigDecimal overtimeHours;
	private int lateHours;
	private int earlyLeaveHours;
	private int leaveDays;
	private int healthInsurance;
	private int laborInsurance;
	private List<Integer> bonuses;


}
