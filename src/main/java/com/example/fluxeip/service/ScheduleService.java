package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
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

		if (existingSchedule == null) {
			return null;
		} else {
			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(existingSchedule);

			return scheduleResponse;
		}
	}

	public List<ScheduleResponse> findSchedulesByEmployeeAndDate(Integer empId, LocalDate date) {

		List<Schedule> schedules = scheduleRepository.findScheduleByEmployeeIdAndDate(empId, date);

		ArrayList<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();

		for (Schedule schedule : schedules) {

			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(schedule);
			responses.add(scheduleResponse);

		}
		return responses;
	}

	public List<ScheduleResponse> findEmpScheduleWeek(Integer empId, String startDate) {

		LocalDate start = LocalDate.parse(startDate);
		start =start.with(DayOfWeek.MONDAY);
		LocalDate end = start.plusDays(6);

		List<Schedule> weeklySchedules = scheduleRepository
				.findByEmployeeEmployeeIdAndScheduleDateBetween(empId,start, end);
		ArrayList<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();
		for (Schedule schedule : weeklySchedules) {
			
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
		
		LocalDate date=scheduleRequest.getDate();

		if(scheduleRepository.countByEmployeeAndDate(empId,date)>0) {
			throw new RuntimeException("該日期已有排班");
		}else if(!isRightDepartment(employee, depName, shiftType)){
			throw new RuntimeException("部門錯誤");
		}else {
			schedule.setDepartment(dep);
			schedule.setEmployee(employee);
			schedule.setShiftType(shiftType);

			schedule.setScheduleDate(date);

			scheduleRepository.save(schedule);
		}
		
		List<Schedule> schedules = scheduleRepository
		.findByEmployeeEmployeeIdAndScheduleDateBetween(empId, date.with(DayOfWeek.MONDAY), date.plusDays(6));
		if(isViolatingLaborLaw(schedules)==1) {
			throw new RuntimeException("違反勞基法，不符合一例一休");
		}else if(isViolatingLaborLaw(schedules)==2){
			throw new RuntimeException("違反勞基法，超時工作");
		}

	}

	@Transactional
	public void updateScheduleById(Integer scheduleId, ScheduleRequest scheduleRequest) {

		if (!scheduleRepository.existsById(scheduleId)) {
			throw new RuntimeException("schedule 不存在，無法更新");
		}

		String depName = scheduleRequest.getDepartmentName();
		Department dep = departmentService.findByName(depName);

		Integer empId = scheduleRequest.getEmployeeId();
		Employee employee = employeeService.find(empId);

		Integer shiftId = scheduleRequest.getShiftTypeId();
		ShiftType shiftType = shiftTypeService.findShiftTypeById(shiftId);

		if (isRightDepartment(employee, depName, shiftType)) {
			Schedule existingSchedule = findScheduleById(scheduleId);

			existingSchedule.setDepartment(dep);
			existingSchedule.setEmployee(employee);
			existingSchedule.setShiftType(shiftType);
			existingSchedule.setScheduleDate(scheduleRequest.getDate());

			scheduleRepository.save(existingSchedule);
		} else {
			throw new RuntimeException("部門錯誤");
		}

	}

	@Transactional
	public boolean deleteScheduleById(Integer scheduleId) {

		if (!scheduleRepository.existsById(scheduleId)) {
			throw new RuntimeException("schedule 不存在，無法刪除");
		}

		scheduleRepository.deleteById(scheduleId);
		return true;
	}

	private boolean isRightDepartment(Employee emp, String departmentName, ShiftType shiftType) {

		if (emp.getDepartment().getDepartmentName().equals(departmentName)
				&& shiftType.getDepartment().getDepartmentName().equals(departmentName)) {
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
	
	private int isViolatingLaborLaw(List<Schedule> schedules) {
		
		BigDecimal workhours=new BigDecimal(0);
		for(Schedule s:schedules) {
			workhours=workhours.add(s.getShiftType().getEstimatedHours());
		}
		
		if(schedules.size()>5) {
			return 1;
		}else if(workhours.compareTo(new BigDecimal(40))==1) {
			return 2;
		}
		
		return 0;
	}

}
