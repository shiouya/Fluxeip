package com.example.fluxeip.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "salary_detail")
public class SalaryDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salary_detail_id")
	private int salaryDetailId;

	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@Column(name = "year_month", nullable = false)
	private String yearMonth;

	@Column(name = "monthly_regular_hours", nullable = false)
	private BigDecimal monthlyRegularHours;

	@Column(name = "overtime_hours", nullable = false)
	private BigDecimal overtimeHours;

	@Column(name = "late_hours", nullable = false)
	private Integer lateHours;

	@Column(name = "early_leave_hours", nullable = false)
	private Integer earlyLeaveHours;

	@Column(name = "leave_days", nullable = false)
	private Integer leaveDays;

	@Column(name = "earned_salary", nullable = false)
	private Integer earnedSalary;
	
	@Column(name = "total_bonus", nullable = false)
	private Integer totalBonus;
	
	@Column(name = "health_insurance", nullable = false)
	private Integer healthInsurance;
	
	@Column(name = "labor_insurance", nullable = false)
	private Integer laborInsurance;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "salary_detail_bonus", joinColumns = @JoinColumn(name = "salary_detail_id"), inverseJoinColumns = @JoinColumn(name = "salary_bonus_id"))
	private List<SalaryBonus> bonuses = new ArrayList<>();
}
