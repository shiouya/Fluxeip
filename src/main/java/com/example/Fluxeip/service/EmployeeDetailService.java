package com.example.Fluxeip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Fluxeip.model.EmployeeDetail;
import com.example.Fluxeip.repository.EmployeeDetailRepository;

@Service
public class EmployeeDetailService {

	@Autowired
	private EmployeeDetailRepository empDetRes;

	public EmployeeDetail empDetCreate(EmployeeDetail entity) {
		EmployeeDetail empDet = empDetRes.save(entity);
		return empDet;
	}

}
