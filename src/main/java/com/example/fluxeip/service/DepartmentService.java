package com.example.fluxeip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.repository.DepartmentRepository;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentRepository depRep;

	public Department findByName(String DepartmentName) {
		Optional<Department> department = depRep.findByDepartmentName(DepartmentName);
		if (department != null) {
			return department.get();
		} else {
			return null;
		}
	}

	public List<Department> findAll() {
		List<Department> departments = depRep.findAll();
		return departments;
	}

}
