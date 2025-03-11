package com.example.fluxeip.dto;

import org.springframework.data.domain.Page;

import com.example.fluxeip.model.Employee;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeePageResponse {

	private long count;
	private Page<Employee> lists;

}
