package com.example.fluxeip.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.repository.ShiftTypeRepository;

@Service
public class ShiftTypeService {

	@Autowired
	private ShiftTypeRepository shiftTypeRepository;
	
	public List<ShiftType> findAllShiftType() {
		
		List<ShiftType> allShiftType = shiftTypeRepository.findAll();
		
		return allShiftType;
	}
	
	@Transactional
	public void createShiftType(ShiftType shiftType) {
		
	}
}
