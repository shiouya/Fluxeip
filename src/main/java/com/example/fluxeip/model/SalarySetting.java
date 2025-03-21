package com.example.fluxeip.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "salary_setting")
public class SalarySetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salary_id")
	private Integer salaryId;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "employee_id",nullable = false, unique = true)
	private Employee employee;
	
	@Column(name = "monthly_salary", nullable = true)
	private Integer monthlySalary;
	
	@Column(name = "hourly_wage", nullable = false)
	private Integer hourlyWage;
}
