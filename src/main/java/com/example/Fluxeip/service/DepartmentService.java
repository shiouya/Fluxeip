package com.example.Fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Fluxeip.model.Department;
import com.example.Fluxeip.repository.DepartmentRepository;

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

}
