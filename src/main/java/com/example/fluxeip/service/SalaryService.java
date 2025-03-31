package com.example.fluxeip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.SalaryDefaultSetting;
import com.example.fluxeip.dto.SalaryDetailRequest;
import com.example.fluxeip.dto.SalaryDetailResponse;
import com.example.fluxeip.model.AttendanceViolations;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.model.SalaryBonus;
import com.example.fluxeip.model.SalaryDetail;
import com.example.fluxeip.model.SalarySetting;
import com.example.fluxeip.model.Schedule;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.repository.AttendanceViolationsRepository;
import com.example.fluxeip.repository.LeaveRequestRepository;
import com.example.fluxeip.repository.SalaryBonusRepository;
import com.example.fluxeip.repository.SalaryDetailRepository;
import com.example.fluxeip.repository.SalarySettingRepository;
import com.example.fluxeip.repository.TypeRepository;
import com.example.fluxeip.repository.WorkAdjustmentRequestRepository;

@Service
public class SalaryService {

	@Autowired
	private SalaryBonusRepository bonusRepository;

	@Autowired
	private SalaryDetailRepository detailRepository;

	@Autowired
	private SalarySettingRepository settingRepository;

	@Autowired
	private AttendanceViolationsRepository violationsRepository;

	@Autowired
	private LeaveRequestRepository leaveRequestRepository;

	@Autowired
	private WorkAdjustmentRequestRepository workAdjustmentRequestRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private NotifyService notifyService;

	private static final int legalMinimumWage = 190; // 最低工資 190 元/時
	private static final double LABOR_INSURANCE_RATE = 0.125; // 勞保 政府公告可更新
	private static final double HEALTH_INSURANCE_RATE = 0.0517;// 健保

	// 薪資設定
	// 用員工搜尋基礎薪資
	public SalaryDefaultSetting findSalarySettingByEmpid(Integer empId) {

		Employee employee = employeeService.find(empId);
		return changeToResponse(settingRepository.findByEmployee(employee));
	}

	// 搜尋全部基礎薪資
	public List<SalaryDefaultSetting> findAllSalarySetting() {
		List<SalarySetting> all = settingRepository.findAll();
		ArrayList<SalaryDefaultSetting> list = new ArrayList<SalaryDefaultSetting>();

		for (SalarySetting salary : all) {
			list.add(changeToResponse(salary));
		}

		return list;
	}

	// 設定基礎薪資
	@Transactional
	public void settingDefaultSalary(SalaryDefaultSetting setting) {

		SalarySetting salary = salaryDefaultSettingRequsetToObject(setting);

		boolean exist = settingRepository.existsByEmployeeEmployeeId(setting.getEmployeeID());

		if (exist) {
			throw new RuntimeException("該員工已設定薪資");
		} else {
			settingRepository.save(salary);
		}
	}

	// 更新基礎薪資
	@Transactional
	public void updateSalarySetting(Integer empId, SalaryDefaultSetting setting) {

		Employee employee = employeeService.find(empId);
		SalarySetting existSalary = settingRepository.findByEmployee(employee);

		if (existSalary != null) {
			if (existSalary.getSalaryId() == setting.getSalaryId()) {
				SalarySetting salary = salaryDefaultSettingRequsetToObject(setting);

				settingRepository.save(salary);
			} else {
				throw new RuntimeException("ID錯誤");
			}
		} else {
			throw new RuntimeException("員工沒有設定薪資");
		}

	}

	// 刪除基礎薪資
	@Transactional
	public boolean deleteSalarySettingByEmpId(Integer empId) {
		Employee employee = employeeService.find(empId);

		SalarySetting setting = settingRepository.findByEmployee(employee);

		setting.setEmployee(null);
		settingRepository.delete(setting);
		return true;
	}

	// 計算時薪
	public Integer caculateHourlyWage(Integer monthlySalary) {

		int hourlyWage = Math.round(Math.round(monthlySalary / 30.0f) / 8.0f);

		if (hourlyWage < legalMinimumWage) {
			return legalMinimumWage;
		}
		return hourlyWage;
	}

	// 把request轉成物件
	private SalarySetting salaryDefaultSettingRequsetToObject(SalaryDefaultSetting setting) {

		SalarySetting salarySetting = new SalarySetting();

		Employee employee = employeeService.find(setting.getEmployeeID());

		salarySetting.setEmployee(employee);
		salarySetting.setHourlyWage(setting.getHourlyWage());
		salarySetting.setMonthlySalary(setting.getMonthlySalary());
		salarySetting.setSalaryId(setting.getSalaryId());

		return salarySetting;
	}

	// 把物件轉成response
	private SalaryDefaultSetting changeToResponse(SalarySetting salarySetting) {
		SalaryDefaultSetting setting = new SalaryDefaultSetting();

		setting.setEmployeeID(salarySetting.getEmployee().getEmployeeId());
		setting.setHourlyWage(salarySetting.getHourlyWage());
		setting.setMonthlySalary(salarySetting.getMonthlySalary());
		setting.setSalaryId(salarySetting.getSalaryId());

		return setting;
	}

	// 薪資結算
	// 用員工找薪資明細
	public List<SalaryDetailResponse> findAllSalaryDetailByEmpId(int empId) {

		Employee employee = employeeService.find(empId);

		List<SalaryDetail> details = detailRepository.findByEmployee(employee);

		if (details == null || details.size() == 0) {
			throw new RuntimeException("找不到薪資明細");
		}

		ArrayList<SalaryDetailResponse> response = new ArrayList<SalaryDetailResponse>();

		for (SalaryDetail detail : details) {
			SalaryDetailResponse detailResponse = detailResponse(detail);
			response.add(detailResponse);
		}
		return response;
	}

	// 找全部明細
	public List<SalaryDetailResponse> findAllSalaryDetail() {
		List<SalaryDetail> details = detailRepository.findAll();

		if (details == null || details.size() == 0) {
			throw new RuntimeException("找不到薪資明細");
		}

		ArrayList<SalaryDetailResponse> response = new ArrayList<SalaryDetailResponse>();

		for (SalaryDetail detail : details) {
			SalaryDetailResponse detailResponse = detailResponse(detail);
			response.add(detailResponse);
		}
		return response;
	}

	// 結算月薪並且存入資料庫
	@Transactional
	public void monthlySalaryCaculate(SalaryDetailRequest request) {

		SalaryDetail salaryDetail = salaryRequestToDetail(request);
		int empId = request.getEmployeeId();
		Employee employee = employeeService.find(empId);

		List<SalaryDetail> existDetail = detailRepository.findByYearMonthAndEmployee(request.getYearMonth(), employee);

		if (employee != null) {
			if (existDetail.size() == 0 || existDetail == null) {
				detailRepository.save(salaryDetail);
				
				 // 發送通知
	            String message = "您的 " + request.getYearMonth() + " 薪資已完成結算，可至薪資查詢頁面查看明細。";
	            notifyService.sendNotification(empId, message);
						
				
			} else {
				throw new RuntimeException("該月份已結算");
			}
		} else {
			throw new RuntimeException("ID錯誤");
		}
	}

	// 刪除明細
	@Transactional
	public boolean deleteDetailById(Integer id) {
		Optional<SalaryDetail> detail = detailRepository.findById(id);
		SalaryDetail existDetail = detail.orElse(null);
		if (existDetail != null) {
			detailRepository.delete(existDetail);
			return true;
		} else {
			new RuntimeException("找不到明細");
			return false;
		}
	}

	// request轉物件
	private SalaryDetail salaryRequestToDetail(SalaryDetailRequest request) {

		SalaryDetail salaryDetail = new SalaryDetail();

		List<SalaryBonus> bonuses = bonusRepository.findAllById(request.getBonuses());
		salaryDetail.setBonuses(bonuses);
		salaryDetail.setEarlyLeaveHours(request.getEarlyLeaveHours());

		Employee employee = employeeService.find(request.getEmployeeId());
		salaryDetail.setEmployee(employee);

		salaryDetail.setLateHours(request.getLateHours());
		salaryDetail.setLeaveDays(request.getLeaveDays());
		salaryDetail.setLaborInsurance(request.getLaborInsurance());
		salaryDetail.setHealthInsurance(request.getHealthInsurance());
		salaryDetail.setMonthlyRegularHours(request.getMonthlyRegularHours());
		salaryDetail.setOvertimeHours(request.getOvertimeHours());
		salaryDetail.setYearMonth(request.getYearMonth());
		salaryDetail.setTotalBonus(countTotalBonus(request) + request.getYearEnd());

		salaryDetail.setEarnedSalary(caculateEarnedSalary(request));
		return salaryDetail;
	}

	// 加班費
	private Integer overtimeSalary(BigDecimal overtimeHours, Integer hourlyWage) {
		Integer overtimeSalary = 0;

		overtimeSalary = overtimeHours.multiply(new BigDecimal(hourlyWage).multiply(new BigDecimal(1.5))).intValue();

		return overtimeSalary;
	}

	// 總月薪
	public Integer caculateEarnedSalary(SalaryDetailRequest request) {

		Integer earnedSalary = 0;
		// 總獎金
		Integer totalBonus = 0;

		totalBonus = countTotalBonus(request);

		// 月薪
		Integer monthlySalary = findSalarySettingByEmpid(request.getEmployeeId()).getMonthlySalary();
		Integer hourlyWage = findSalarySettingByEmpid(request.getEmployeeId()).getHourlyWage();
		Integer salary = 0;
		if (monthlySalary.equals(0)) {
			BigDecimal roundedValue = request.getMonthlyRegularHours().multiply(new BigDecimal(hourlyWage)).setScale(2,
					RoundingMode.HALF_UP);
			salary = roundedValue.intValue();
		} else {
			salary = monthlySalary;
		}

		// 加班費
		BigDecimal overtimeHours = request.getOvertimeHours();
		Integer overtimeSalary = overtimeSalary(overtimeHours, hourlyWage);

		// 遲到早退請假
		Integer earlyLeaveHours = request.getEarlyLeaveHours();
		Integer lateHours = request.getLateHours();
		Integer leaveDays = request.getLeaveDays();
		Integer earlyLateLeave = (int) Math
				.round(((double) earlyLeaveHours / 2 + (double) lateHours / 2 + leaveDays) * hourlyWage);

		// 年終
		int yearEnd = request.getYearEnd();
		// 月薪(時薪*工時)+bonus-勞保-健保-(遲到+早退)*時薪-請假*時薪+加班費+年終
		earnedSalary = salary + totalBonus - request.getLaborInsurance() - request.getHealthInsurance() + overtimeSalary
				- earlyLateLeave + yearEnd;

		return earnedSalary;
	}

	// 計算bonus
	private Integer countTotalBonus(SalaryDetailRequest request) {

		List<SalaryBonus> bonuses = bonusRepository.findAllById(request.getBonuses());
		Integer totalBonus = 0;

		for (SalaryBonus bonus : bonuses) {
			totalBonus += bonus.getAmount();
		}

		return totalBonus;
	}

	// 產生response
	public SalaryDetailResponse detailResponse(SalaryDetail salaryDetail) {
		SalaryDetailResponse response = new SalaryDetailResponse();

		List<SalaryBonus> bonuses = salaryDetail.getBonuses();

		if (bonuses == null || bonuses.size() == 0) {
			response.setBonuses(null);
		} else {
			response.setBonuses(bonuses);
		}
		response.setEarlyLeaveHours(salaryDetail.getEarlyLeaveHours());

		Integer employeeId = salaryDetail.getEmployee().getEmployeeId();
		response.setEmployeeId(employeeId);
		response.setEmployeeName(salaryDetail.getEmployee().getEmployeeName());
		response.setDepartment(salaryDetail.getEmployee().getDepartment().getDepartmentName());

		response.setHealthInsurance(salaryDetail.getHealthInsurance());
		response.setLaborInsurance(salaryDetail.getLaborInsurance());

		response.setLeaveDaysHoursByType(leaveDaysHoursByType(employeeId, salaryDetail.getYearMonth()));
		response.setLateHours(salaryDetail.getLateHours());
		response.setLeaveDays(salaryDetail.getLeaveDays());
		response.setMonthlyRegularHours(salaryDetail.getMonthlyRegularHours());
		response.setOvertimeHours(salaryDetail.getOvertimeHours());
		response.setSalaryDetailId(salaryDetail.getSalaryDetailId());
		response.setEarnedSalary(salaryDetail.getEarnedSalary());

		Integer bonusWithoutYearEnd = 0;

		for (SalaryBonus bonus : bonuses) {
			bonusWithoutYearEnd += bonus.getAmount();
		}
		Integer totalBonus = salaryDetail.getTotalBonus();
		response.setYearEnd(totalBonus - bonusWithoutYearEnd);

		response.setYearMonth(salaryDetail.getYearMonth());

		return response;
	}

	// 月總工時
	public BigDecimal countMonthlyWorkHours(Integer empId, String yearMonthStr) {
		Double workHpurs = 0.0;

		YearMonth yearMonth = YearMonth.parse(yearMonthStr);
		LocalDate startOfMonth = yearMonth.atDay(1).atStartOfDay().toLocalDate();
		LocalDate endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX).toLocalDate();

		List<Schedule> schedules = scheduleService.schedulesInInterval(empId, startOfMonth, endOfMonth);

		for (Schedule schedule : schedules) {
			workHpurs += schedule.getShiftType().getEstimatedHours().doubleValue();
		}

		Map<String, Integer> overtimeAndMinus = overtimeAndMinus(empId, yearMonthStr);
		Integer minus = overtimeAndMinus.get("minusHours");

		BigDecimal monthlyWorkHours = new BigDecimal(workHpurs);

		return monthlyWorkHours.subtract(new BigDecimal(minus));
	}

	// 遲到及早退時數(半小時)
	public Map<String, Integer> countMonthlyLateAndEarlyLeaveByEmpId(Integer empId, String yearMonthStr) {

		Integer lateHour = 0;
		Integer earlyLeaveHour = 0;
		YearMonth yearMonth = YearMonth.parse(yearMonthStr);
		LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

		List<AttendanceViolations> lates = violationsRepository.findViolationsByEmployeeAndTypeAndMonth(empId, "遲到",
				startOfMonth, endOfMonth);
		List<AttendanceViolations> earlyLeaves = violationsRepository.findViolationsByEmployeeAndTypeAndMonth(empId,
				"早退", startOfMonth, endOfMonth);

		for (AttendanceViolations late : lates) {
			lateHour += (int) Math.ceil((double) late.getViolationMinutes() / 30);
		}
		for (AttendanceViolations early : earlyLeaves) {
			earlyLeaveHour += (int) Math.ceil((double) early.getViolationMinutes() / 30);
		}

		HashMap<String, Integer> hours = new HashMap<String, Integer>();
		hours.put("lateHour", lateHour);
		hours.put("earlyLeaveHour", earlyLeaveHour);

		return hours;
	}

	// 請假時數
	public Double leaveDaysHours(Integer empId, String yearMonthStr) {
		Double hours = 0.0;
		YearMonth yearMonth = YearMonth.parse(yearMonthStr);
		LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
		List<LeaveRequest> days = leaveRequestRepository.findByEmpidAndStatusAndDateRange(empId, "已核決", startOfMonth,
				endOfMonth);

		for (LeaveRequest day : days) {
			if ("事假".equals(day.getLeaveType().getTypeName()) || "家庭照顧假".equals(day.getLeaveType().getTypeName())) {
				hours += day.getLeaveHours().intValue();
			} else if ("生理假".equals(day.getLeaveType().getTypeName())
					|| "病假".equals(day.getLeaveType().getTypeName())) {
				hours += day.getLeaveHours().divide(new BigDecimal(2)).doubleValue();
			}
		}
		return hours;
	}

	// 請假時數(種類別)
	public Map<String, Double> leaveDaysHoursByType(Integer empId, String yearMonthStr) {

		YearMonth yearMonth = YearMonth.parse(yearMonthStr);
		LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

		List<LeaveRequest> days = leaveRequestRepository.findByEmpidAndStatusAndDateRange(empId, "已核決", startOfMonth,
				endOfMonth);
		List<Type> types = typeRepository.findByCategory("leave_type");

		HashMap<String, Double> hoursByType = new HashMap<String, Double>();

		for (Type type : types) {
			Double hours = 0.0;
			for (LeaveRequest day : days) {
				if (type.getTypeName().equals(day.getLeaveType().getTypeName())) {
					hours += day.getLeaveHours().intValue();
				}
			}
			if (hours != 0.0) {
				hoursByType.put(type.getTypeName(), hours);
			}
		}
		if (hoursByType.size() == 0 || hoursByType == null) {
			return null;
		}
		return hoursByType;
	}

	// 加班減班
	public Map<String, Integer> overtimeAndMinus(Integer empId, String yearMonthStr) {
		YearMonth yearMonth = YearMonth.parse(yearMonthStr);
		LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

		Integer overtimeHours = 0;
		Integer minusHours = 0;

		List<WorkAdjustmentRequest> overtimes = workAdjustmentRequestRepository
				.findWorkAdjustmentRequestByEmployeeAndTypeAndMonth(empId, "加班", startOfMonth, endOfMonth);
		List<WorkAdjustmentRequest> minuses = workAdjustmentRequestRepository
				.findWorkAdjustmentRequestByEmployeeAndTypeAndMonth(empId, "減班", startOfMonth, endOfMonth);

		for (WorkAdjustmentRequest requset : overtimes) {
			overtimeHours += requset.getHours().intValue();
		}
		for (WorkAdjustmentRequest requset : minuses) {
			minusHours += requset.getHours().intValue();
		}

		HashMap<String, Integer> hours = new HashMap<String, Integer>();
		hours.put("overtimeHours", overtimeHours);
		hours.put("minusHours", minusHours);

		return hours;
	}

	// 勞健保
	public Map<String, Integer> laborInsuranceAndHealthInsurance(Integer salary) {

		Integer healthInsurance = 0;
		Integer laborInsurance = 0;
		int[] grade = { 28590/* 勞建保最低0 */, 28800, 30300, 31800, 33300, 34800, 36300, 38200, 40100, 42000, 43900,
				45800/* 勞保最高11 */, 48200, 50600, 53000, 55400, 57800, 60800, 63800, 66800, 69800, 72800, 76500, 80200,
				83900, 87600, 92100, 96600, 101100, 105600, 110100, 115500, 120900, 126300, 131700, 137100, 142500,
				147900, 150000, 156400, 162800, 169200, 175600, 182000, 189500, 197000, 204500, 212000, 219500, 228200,
				236900, 245600, 254300, 263000, 273000, 283000, 293000, 303000, 313000/* 建保最高58 */ };
		if (salary <= grade[0]) {
			healthInsurance = (int) Math.round(HEALTH_INSURANCE_RATE * grade[0] * 0.3f);
			laborInsurance = (int) Math.round(LABOR_INSURANCE_RATE * grade[0] * 0.2f);
		} else if (salary > grade[0] && salary <= grade[11]) {
			for (int i = 1; i <= 11; i++) {
				if (salary <= grade[i]) {
					healthInsurance = (int) Math.round(HEALTH_INSURANCE_RATE * grade[i] * 0.3f);
					laborInsurance = (int) Math.round(LABOR_INSURANCE_RATE * grade[i] * 0.2f);
					break;
				}
			}
		} else if (salary > grade[11] && salary <= grade[58]) {
			for (int i = 12; i < grade.length; i++) {
				if (salary <= grade[i]) {
					healthInsurance = (int) Math.round(HEALTH_INSURANCE_RATE * grade[i] * 0.3f);
					laborInsurance = (int) Math.round(LABOR_INSURANCE_RATE * grade[11] * 0.2f);
					break;
				}
			}
		} else {
			healthInsurance = (int) Math.round(HEALTH_INSURANCE_RATE * grade[58] * 0.3f);
			laborInsurance = (int) Math.round(LABOR_INSURANCE_RATE * grade[11] * 0.2f);
		}

		HashMap<String, Integer> insurance = new HashMap<String, Integer>();
		insurance.put("healthInsurance", healthInsurance);
		insurance.put("laborInsurance", laborInsurance);

		return insurance;

	}

	// 獎金津貼
	public List<SalaryBonus> findAllBonus() {
		return bonusRepository.findByIsActiveTrueOrderByBonusTypeAsc();
	}

	// 修改獎金津貼數字
	@Transactional
	public void updateBonus(Integer id, Integer amount) {
		SalaryBonus bonus = bonusRepository.findBySalaryBonusIdAndIsActiveTrue(id);

		if (bonus == null) {
			throw new RuntimeException("發生錯誤");
		}
		bonus.setActive(false);

		bonusRepository.save(bonus);

		SalaryBonus newSalaryBonus = new SalaryBonus();
		newSalaryBonus.setAmount(amount);
		newSalaryBonus.setBonusType(bonus.getBonusType());
		newSalaryBonus.setActive(true);

		deleteBonusFromSql(id);
		bonusRepository.save(newSalaryBonus);
	}

	// 修改獎金津貼名稱
	@Transactional
	public void updateBonusName(Integer id, String name) {
		SalaryBonus bonus = bonusRepository.findBySalaryBonusIdAndIsActiveTrue(id);

		if (bonus == null) {
			throw new RuntimeException("發生錯誤");
		}
		bonus.setActive(false);

		bonusRepository.save(bonus);

		SalaryBonus newSalaryBonus = new SalaryBonus();
		newSalaryBonus.setAmount(bonus.getAmount());
		newSalaryBonus.setBonusType(name);
		newSalaryBonus.setActive(true);

		deleteBonusFromSql(id);
		bonusRepository.save(newSalaryBonus);
	}
	
	//新增獎金津貼
	@Transactional
	public void insertNewBonus(String name,Integer amount) {
		
		List<SalaryBonus> existBonuses = bonusRepository.findByIsActiveTrue();
		
		for(SalaryBonus bonus:existBonuses) {
			if(name.equals(bonus.getBonusType())) {				
				throw new RuntimeException("已經有設定該獎金/津貼");
			}
		}
		
		SalaryBonus newBonus = new SalaryBonus();
		newBonus.setAmount(amount);
		newBonus.setBonusType(name);
		newBonus.setActive(true);
		
		bonusRepository.save(newBonus);
	}
	
	//獎金津貼改為不可用
	@Transactional
	public void deleteBonusById(Integer id) {
		SalaryBonus bonus = bonusRepository.findBySalaryBonusIdAndIsActiveTrue(id);

		if (bonus == null) {
			throw new RuntimeException("發生錯誤");
		}
		
		bonus.setActive(false);
		
		bonusRepository.save(bonus);
		
		deleteBonusFromSql(id);
	}
	
	//從資料庫刪除獎金津貼
	@Transactional
	private void deleteBonusFromSql(Integer id) {
		boolean exists = bonusRepository.existsInSalaryDetail(id);
		
		if(!exists) {
			bonusRepository.deleteById(id);
		}
	}
}
