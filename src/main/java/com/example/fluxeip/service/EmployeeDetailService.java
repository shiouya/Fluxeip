package com.example.fluxeip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.EmployeeDetail;
import com.example.fluxeip.repository.EmployeeDetailRepository;

@Service
public class EmployeeDetailService {

	@Autowired
	private EmployeeDetailRepository empDetRes;

	public EmployeeDetail empDetCreate(EmployeeDetail entity) {
		EmployeeDetail empDet = empDetRes.save(entity);
		return empDet;
	}

}
