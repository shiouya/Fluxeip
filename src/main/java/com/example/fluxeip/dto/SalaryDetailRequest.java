package com.example.fluxeip.dto;


import java.math.BigDecimal;
import java.util.List;

import com.example.fluxeip.model.SalaryBonus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryDetailRequest {

	private Integer salaryDetailId;
	private Integer employeeId;
	private String yearMonth;
	private BigDecimal monthlyRegularHours;
	private BigDecimal overtimeHours;
	private Integer lateHours;
	private Integer earlyLeaveHours;
	private Integer leaveDays;
	private Integer healthInsurance;
	private Integer laborInsurance;
	private List<Integer> bonuses;
	private Integer yearEnd;

}
