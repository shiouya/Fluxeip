package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.ShiftTypeRequest;
import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.repository.ShiftTypeRepository;

@Service
public class ShiftTypeService {

	@Autowired
	private ShiftTypeRepository shiftTypeRepository;

	@Autowired
	private DepartmentService departmentService;

	public List<ShiftType> findAllShiftType() {

		List<ShiftType> allShiftType = shiftTypeRepository.findAll();

		return allShiftType;
	}

	@Transactional
	public void createShiftType(ShiftTypeRequest shiftTypeRequest) {
		ShiftType shiftType = new ShiftType();

		Department department = departmentService.findByName(shiftTypeRequest.getDepartmentName());

		shiftType.setDepartment(department);

		LocalTime start = shiftTypeRequest.getStartTime();
		LocalTime finish = shiftTypeRequest.getFinishTime();

		BigDecimal minutes = new BigDecimal(Duration.between(start, finish).toMinutes());

		BigDecimal estimatedHours = minutes.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		if (estimatedHours.compareTo(new BigDecimal(4)) == 1) {
			if (estimatedHours.compareTo(new BigDecimal(8)) == 1) {
				estimatedHours = estimatedHours.subtract(new BigDecimal(1));
			}else {
				estimatedHours = estimatedHours.subtract(new BigDecimal(0.5));
			}
		}

		shiftType.setStartTime(start);
		shiftType.setFinishTime(finish);
		shiftType.setShiftCategory(shiftTypeRequest.getShiftCategory());
		shiftType.setShiftName(shiftTypeRequest.getShiftName());
		shiftType.setEstimatedHours(estimatedHours);

		shiftTypeRepository.save(shiftType);

	}
}
