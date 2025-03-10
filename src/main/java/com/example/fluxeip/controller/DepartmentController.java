package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.service.DepartmentService;

@RestController
public class DepartmentController {

	@Autowired
	private DepartmentService depSer;

	@GetMapping("/department/find")
	public List<Department> departmentFindAll() {
		List<Department> departments = depSer.findAll();
		return departments;
	}

}
