package com.example.fluxeip.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.ScheduleRequest;
import com.example.fluxeip.dto.ScheduleResponse;
import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Schedule;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.repository.ScheduleRepository;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ShiftTypeService shiftTypeService;
	
	public Schedule findScheduleById(Integer schedulId) {
		Optional<Schedule> schedule = scheduleRepository.findById(schedulId);

		return schedule.orElse(null);
	}
	
	public ScheduleResponse findScheduleResponseById(Integer schedulId) {
		Optional<Schedule> schedule = scheduleRepository.findById(schedulId);

		Schedule existingSchedule = schedule.orElse(null);
		
		if(existingSchedule==null) {
			return null;
		}else {
			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(existingSchedule);
			
			return scheduleResponse;
		}
	}
	
	public List<ScheduleResponse> findSchedulesByEmployeeAndDate(Integer empId, LocalDate date) {
		
		List<Schedule> schedules = scheduleRepository.findScheduleByEmployeeIdAndDate(empId, date);
		
		ArrayList<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();

		for(Schedule schedule:schedules) {
			
			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(schedule);
			responses.add(scheduleResponse);
			
		}
		return responses;
	}
	
	@Transactional
	public void createSchedule(ScheduleRequest scheduleRequest) {
		
		Schedule schedule = new Schedule();
		
		String depName = scheduleRequest.getDepartmentName();
		Department dep = departmentService.findByName(depName);
		
		Integer empId = scheduleRequest.getEmployeeId();
		Employee employee = employeeService.find(empId);
		
		Integer shiftId = scheduleRequest.getShiftTypeId();
		ShiftType shiftType = shiftTypeService.findShiftTypeById(shiftId);

		if(isRightDepartment(employee, depName, shiftType)) {
			schedule.setDepartment(dep);
			schedule.setEmployee(employee);
			schedule.setShiftType(shiftType);

			schedule.setScheduleDate(scheduleRequest.getDate());
			
			scheduleRepository.save(schedule);
		}else {
			throw new RuntimeException("部門錯誤");
		}

	}
	
	@Transactional
	public void updateScheduleById(Integer scheduleId,ScheduleRequest scheduleRequest) {
		
		if(!scheduleRepository.existsById(scheduleId)) {
			throw new RuntimeException("schedule 不存在，無法更新");
		}
		
		String depName = scheduleRequest.getDepartmentName();
		Department dep = departmentService.findByName(depName);
		
		Integer empId = scheduleRequest.getEmployeeId();
		Employee employee = employeeService.find(empId);
		
		Integer shiftId = scheduleRequest.getShiftTypeId();
		ShiftType shiftType = shiftTypeService.findShiftTypeById(shiftId);
		
		if(isRightDepartment(employee, depName, shiftType)) {
			Schedule existingSchedule = findScheduleById(scheduleId);
			
			existingSchedule.setDepartment(dep);
			existingSchedule.setEmployee(employee);
			existingSchedule.setShiftType(shiftType);
			existingSchedule.setScheduleDate(scheduleRequest.getDate());
			
			scheduleRepository.save(existingSchedule);
		}else {
			throw new RuntimeException("部門錯誤");
		}
		
	}
	
	@Transactional
	public boolean deleteScheduleById(Integer scheduleId) {
		
		if(!scheduleRepository.existsById(scheduleId)) {
			throw new RuntimeException("schedule 不存在，無法刪除");
		}
				
		scheduleRepository.deleteById(scheduleId);
		return true;
	}
	
	private boolean isRightDepartment(Employee emp,String departmentName,ShiftType shiftType) {
		
		if(emp.getDepartment().getDepartmentName().equals(departmentName)
				&&shiftType.getDepartment().getDepartmentName().equals(departmentName)) {
			return true;
		}
		return false;
	}
	
	private ScheduleResponse changeScheduleIntoResponse(Schedule schedule) {
		ScheduleResponse scheduleResponse = new ScheduleResponse();
		
		scheduleResponse.setDate(schedule.getScheduleDate());
		scheduleResponse.setDepartmentName(schedule.getDepartment().getDepartmentName());
		scheduleResponse.setEmployeeName(schedule.getEmployee().getEmployeeName());
		scheduleResponse.setShiftTypeName(schedule.getShiftType().getShiftName());
		
		return scheduleResponse;
		
	}
}
