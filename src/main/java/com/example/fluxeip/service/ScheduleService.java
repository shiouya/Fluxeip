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
import com.example.fluxeip.repository.EmployeeRepository;
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
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private NotifyService notifyService;

	// 透過id搜尋班表
	public Schedule findScheduleById(Integer schedulId) {
		Optional<Schedule> schedule = scheduleRepository.findById(schedulId);

		return schedule.orElse(null);
	}

	// 透過id搜尋response
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

	// 透過員工搜尋response
	public List<ScheduleResponse> findScheduleResponseByEmpId(Integer empId) {
		List<Schedule> schedules = scheduleRepository.findByEmployeeEmployeeId(empId);

		ArrayList<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();

		for (Schedule schedule : schedules) {

			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(schedule);
			responses.add(scheduleResponse);

		}
		return responses;
	}

	// 透過員工和日期搜尋response
	public List<ScheduleResponse> findSchedulesByEmployeeAndDate(Integer empId, LocalDate date) {

		List<Schedule> schedules = scheduleRepository.findScheduleByEmployeeIdAndDate(empId, date);

		ArrayList<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();

		for (Schedule schedule : schedules) {

			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(schedule);
			responses.add(scheduleResponse);

		}
		return responses;
	}

	// 查詢員工一周班表
	public List<ScheduleResponse> findEmpScheduleWeek(Integer empId, String startDate) {

		LocalDate start = LocalDate.parse(startDate);
		start = start.with(DayOfWeek.MONDAY);
		LocalDate end = start.plusDays(6);

		List<Schedule> weeklySchedules = schedulesInInterval(empId, start, end);
		ArrayList<ScheduleResponse> responses = new ArrayList<ScheduleResponse>();
		for (Schedule schedule : weeklySchedules) {

			ScheduleResponse scheduleResponse = changeScheduleIntoResponse(schedule);
			responses.add(scheduleResponse);

		}
		return responses;

	}

	// 新增班表
	@Transactional
	public void createSchedule(ScheduleRequest scheduleRequest) {

		Schedule schedule = new Schedule();

		String depName = scheduleRequest.getDepartmentName();
		Department dep = departmentService.findByName(depName);

		Integer empId = scheduleRequest.getEmployeeId();
		Employee employee = employeeService.find(empId);

		Integer shiftId = scheduleRequest.getShiftTypeId();
		ShiftType shiftType = shiftTypeService.findShiftTypeById(shiftId);

		LocalDate date = scheduleRequest.getDate();

		if (scheduleRepository.countByEmployeeAndDate(empId, date) > 0) {
			throw new RuntimeException("該日期已有排班");
		} else if (!isRightDepartment(employee, depName, shiftType)) {
			throw new RuntimeException("部門錯誤");
		} else {
			schedule.setDepartment(dep);
			schedule.setEmployee(employee);
			schedule.setShiftType(shiftType);

			schedule.setScheduleDate(date);

			scheduleRepository.save(schedule);
		}

		if (isViolatingLaborLawDays(date, empId)) {
			throw new RuntimeException("違反勞基法，不符合一例一休");
		} else if (isViolatingLaborLawHours(date, empId)) {
			throw new RuntimeException("違反勞基法，超時工作");
		}

	}

	// 新增整月班表
	@Transactional
	public void insertMonthlySchedule(ScheduleRequest scheduleRequest) {

		Integer shiftId = scheduleRequest.getShiftTypeId();
		ShiftType shiftType = shiftTypeService.findShiftTypeById(shiftId);

		if (shiftType.getEstimatedHours().multiply(new BigDecimal(5)).compareTo(new BigDecimal(40)) == 1) {
			throw new RuntimeException("該班別可能導致超時工作，無法自動排班，請嘗試其他班別!");
		}
		String depName = scheduleRequest.getDepartmentName();
		Department dep = departmentService.findByName(depName);

		Integer empId = scheduleRequest.getEmployeeId();
		Employee employee = employeeService.find(empId);

		LocalDate date = scheduleRequest.getDate();
		List<LocalDate> weekdaysInMonth = getWeekdaysInMonth(date);
		
		boolean notifySchedule = false; //通知

		System.out.println(weekdaysInMonth);
		for(LocalDate day:weekdaysInMonth) {
			if (scheduleRepository.countByEmployeeAndDate(empId, day) > 0) {
				throw new RuntimeException("該日期已有排班");
			}else {
				Schedule schedule = new Schedule();

				schedule.setDepartment(dep);
				schedule.setEmployee(employee);
				schedule.setShiftType(shiftType);

				schedule.setScheduleDate(day);

				scheduleRepository.save(schedule);
				
				notifySchedule = true;//通知
				
			}

		}
		//通知
		if (notifySchedule) {
			String monthStr = date.getMonthValue() + "月";
			String message = "您在 " + monthStr + " 的班表已排定，請前往班表頁面確認。";
			notifyService.sendNotification(employee.getEmployeeId(), message);
		}
	}

	// 更新班表
	@Transactional
	public void updateScheduleById(Integer scheduleId, Integer shiftTypeId) {

		if (!scheduleRepository.existsById(scheduleId)) {
			throw new RuntimeException("schedule 不存在，無法更新");
		}
		ShiftType shiftType = shiftTypeService.findShiftTypeById(shiftTypeId);

		Schedule existingSchedule = findScheduleById(scheduleId);

		Integer empId = existingSchedule.getEmployee().getEmployeeId();
		LocalDate date = existingSchedule.getScheduleDate();
		existingSchedule.setShiftType(shiftType);

		scheduleRepository.save(existingSchedule);

		if (isViolatingLaborLawDays(date, empId)) {
			throw new RuntimeException("違反勞基法，不符合一例一休");
		} else if (isViolatingLaborLawHours(date, empId)) {
			throw new RuntimeException("違反勞基法，超時工作");
		}

	}

	// 刪除班表
	@Transactional
	public boolean deleteScheduleById(Integer scheduleId) {

		if (!scheduleRepository.existsById(scheduleId)) {
			throw new RuntimeException("schedule 不存在，無法刪除");
		}

		scheduleRepository.deleteById(scheduleId);
		return true;
	}

	//刪除整月班表
	@Transactional
	public void deleteMonthSchedule(Integer empId,LocalDate firstDay) {
		
		LocalDate firstDayOfMonth = firstDay.withDayOfMonth(1);
		LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
		
		List<Schedule> schedulesInInterval = schedulesInInterval(empId, firstDayOfMonth, lastDayOfMonth);
		
		if(schedulesInInterval==null||schedulesInInterval.size()==0) {
			throw new RuntimeException("查無本月班表，無法刪除");
		}else {
			for(Schedule schedule:schedulesInInterval) {
				scheduleRepository.delete(schedule);
			}
		}
	}
	
	// 判斷部門
	private boolean isRightDepartment(Employee emp, String departmentName, ShiftType shiftType) {

		if (emp.getDepartment().getDepartmentName().equals(departmentName)
				&& shiftType.getDepartment().getDepartmentName().equals(departmentName)) {
			return true;
		}
		return false;
	}

	// 轉換成response
	private ScheduleResponse changeScheduleIntoResponse(Schedule schedule) {
		ScheduleResponse scheduleResponse = new ScheduleResponse();

		scheduleResponse.setScheduleId(schedule.getScheduleId());
		scheduleResponse.setDate(schedule.getScheduleDate());
		scheduleResponse.setDepartmentName(schedule.getDepartment().getDepartmentName());
		scheduleResponse.setEmployeeName(schedule.getEmployee().getEmployeeName());
		scheduleResponse.setShiftTypeName(schedule.getShiftType().getShiftName());

		return scheduleResponse;

	}

	// 違反一例一休
	private boolean isViolatingLaborLawDays(LocalDate date, int empId) {

		for (int i = 0; i < 7; i++) {
			List<Schedule> sevenDays = schedulesInInterval(empId, date.minusDays(6 - i), date.plusDays(i));

			if (sevenDays.size() > 6) {
				return true;
			}
		}

		return false;
	}

	// 超時工作
	private boolean isViolatingLaborLawHours(LocalDate date, int empId) {

		List<Schedule> schedules = schedulesInInterval(empId, date.with(DayOfWeek.MONDAY),
				date.with(DayOfWeek.MONDAY).plusDays(6));

		BigDecimal workhours = new BigDecimal(0);
		for (Schedule s : schedules) {
			workhours = workhours.add(s.getShiftType().getEstimatedHours());
		}

		if (workhours.compareTo(new BigDecimal(40)) == 1) {
			return true;
		}

		return false;
	}

	// 透過時間區間查詢班表
	public List<Schedule> schedulesInInterval(int employeeId, LocalDate startDate, LocalDate endDate) {
		return scheduleRepository.findByEmployeeEmployeeIdAndScheduleDateBetween(employeeId, startDate, endDate);
	}

	// 透過部門搜尋全體員工
	public List<Employee> findAllEmpByDepartmentId(Integer departmentId) {
		return employeeRepository.findByDepartmentDepartmentId(departmentId);
	}

	// 找平日
	private static List<LocalDate> getWeekdaysInMonth(LocalDate date) {
		List<LocalDate> weekdays = new ArrayList<>();

		// 獲取該月的第一天和最後一天
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

		// 遍歷該月的所有日期
		LocalDate currentDay = firstDayOfMonth;
		while (!currentDay.isAfter(lastDayOfMonth)) {
			// 如果是平日（不是週六或週日），加入列表
			if (currentDay.getDayOfWeek() != DayOfWeek.SATURDAY && currentDay.getDayOfWeek() != DayOfWeek.SUNDAY) {
				weekdays.add(currentDay);
			}
			// 移動到下一天
			currentDay = currentDay.plusDays(1);
		}

		return weekdays;
	}

}
