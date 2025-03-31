package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.ShiftTypeRequest;
import com.example.fluxeip.dto.ShiftTypeResponse;
import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.repository.ShiftTypeRepository;

@Service
public class ShiftTypeService {

	@Autowired
	private ShiftTypeRepository shiftTypeRepository;

	@Autowired
	private DepartmentService departmentService;

	public List<ShiftTypeResponse> findAllShiftType() {

		List<ShiftType> allShiftType = shiftTypeRepository.findByIsActiveTrueOrderByDepartment();

		ArrayList<ShiftTypeResponse> responses = new ArrayList<ShiftTypeResponse>();
		
		for(ShiftType shiftType:allShiftType) {
			responses.add(toResponse(shiftType));
		}
		return responses;
	}
	
	public ShiftType findShiftTypeById(Integer shiftTypeId) {
		Optional<ShiftType> shiftType = shiftTypeRepository.findById(shiftTypeId);
		return shiftType.orElse(null);
	}
	
	public ShiftTypeResponse findShiftTypeByIdToResponse(Integer shiftTypeId) {
		Optional<ShiftType> shiftType = shiftTypeRepository.findById(shiftTypeId);
		
		return toResponse(shiftType.orElse(null));
	}

	@Transactional
	public void createShiftType(ShiftTypeRequest shiftTypeRequest) {
		ShiftType shiftType = new ShiftType();

		Department department = departmentService.findByName(shiftTypeRequest.getDepartmentName());

		shiftType.setDepartment(department);

		LocalTime start = shiftTypeRequest.getStartTime();
		LocalTime finish = shiftTypeRequest.getFinishTime();

		BigDecimal estimatedHours = estimatedHoursCompute(start, finish);

		shiftType.setStartTime(start);
		shiftType.setFinishTime(finish);
		shiftType.setShiftCategory(shiftTypeRequest.getShiftCategory());
		shiftType.setShiftName(shiftTypeRequest.getShiftName());
		shiftType.setEstimatedHours(estimatedHours);
		shiftType.setActive(true);
		shiftTypeRepository.save(shiftType);

	}
	
	@Transactional
	public void updateShiftTypeById(Integer shiftTypeId,ShiftTypeRequest shiftTypeRequest) {
		
		ShiftType existingShiftType = findShiftTypeById(shiftTypeId);
		
		if(existingShiftType==null) {
			throw new RuntimeException("ShiftType 不存在，無法更新");
		}
		
		Department department = departmentService.findByName(shiftTypeRequest.getDepartmentName());

		existingShiftType.setShiftTypeId(shiftTypeId);
		existingShiftType.setDepartment(department);
		existingShiftType.setShiftName(shiftTypeRequest.getShiftName());
		existingShiftType.setShiftCategory(shiftTypeRequest.getShiftCategory());
		existingShiftType.setStartTime(shiftTypeRequest.getStartTime());
		existingShiftType.setFinishTime(shiftTypeRequest.getFinishTime());
		existingShiftType.setEstimatedHours(estimatedHoursCompute(shiftTypeRequest.getStartTime(),shiftTypeRequest.getFinishTime()));
		
		
		shiftTypeRepository.save(existingShiftType);
	}
	
	@Transactional
	public boolean deleteShiftTypeById(Integer shiftTypeId) {
		
		if(!shiftTypeRepository.existsById(shiftTypeId)) {
			throw new RuntimeException("ShiftType 不存在，無法刪除");
		}
		
		Optional<ShiftType> byId = shiftTypeRepository.findById(shiftTypeId);
		ShiftType shiftType = byId.orElse(null);
		
		if(shiftType==null) {
			throw new RuntimeException("ShiftType 不存在，無法刪除");
		}
		shiftType.setActive(false);
		return true;
	}
	
	
	private BigDecimal estimatedHoursCompute(LocalTime start,LocalTime finish) {
		
		BigDecimal minutes = new BigDecimal(Duration.between(start, finish).toMinutes());

		BigDecimal estimatedHours = minutes.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		if (estimatedHours.compareTo(new BigDecimal(4)) == 1) {
			if (estimatedHours.compareTo(new BigDecimal(8)) == 1) {
				estimatedHours = estimatedHours.subtract(new BigDecimal(1));
			}else {
				estimatedHours = estimatedHours.subtract(new BigDecimal(0.5));
			}
		}
		
		return estimatedHours;
	}
	
	private ShiftTypeResponse toResponse(ShiftType shiftType) {
		
		if(shiftType!=null) {
			ShiftTypeResponse shiftTypeResponse = new ShiftTypeResponse();
			
			shiftTypeResponse.setShiftTypeId(shiftType.getShiftTypeId());
			shiftTypeResponse.setShiftName(shiftType.getShiftName());
			shiftTypeResponse.setDepartmentName(shiftType.getDepartment().getDepartmentName());
			shiftTypeResponse.setShiftCategory(shiftType.getShiftCategory());
			shiftTypeResponse.setStartTime(shiftType.getStartTime());
			shiftTypeResponse.setFinishTime(shiftType.getFinishTime());
			shiftTypeResponse.setEstimatedHours(shiftType.getEstimatedHours());
			
			return shiftTypeResponse;
		}
		return null;

	}
}
