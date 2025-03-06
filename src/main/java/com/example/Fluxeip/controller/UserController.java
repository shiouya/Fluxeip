package com.example.Fluxeip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Fluxeip.dto.EmployeeCreateRequest;
import com.example.Fluxeip.dto.EmployeeCreateResponse;
import com.example.Fluxeip.model.Department;
import com.example.Fluxeip.model.Employee;
import com.example.Fluxeip.model.EmployeeDetail;
import com.example.Fluxeip.model.Position;
import com.example.Fluxeip.model.Status;
import com.example.Fluxeip.service.DepartmentService;
import com.example.Fluxeip.service.EmployeeDetailService;
import com.example.Fluxeip.service.EmployeeService;
import com.example.Fluxeip.service.PositionService;
import com.example.Fluxeip.service.StatusService;

@RestController
public class UserController {

	@Autowired
	private StatusService staSer;

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Autowired
	private PositionService posSer;

	@Autowired
	private DepartmentService depSer;

	@Autowired
	private EmployeeService empService;

	@Autowired
	private EmployeeDetailService empDetSer;

	@PostMapping("/employee/create")
	public EmployeeCreateResponse employeeCreate(@RequestBody EmployeeCreateRequest entity) {
		EmployeeCreateResponse empCreRes = new EmployeeCreateResponse();
		Employee employee = new Employee();
		EmployeeDetail empDet = new EmployeeDetail();
		if (entity.getEmployeeName() == null || entity.getEmployeeName().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工姓名");
		} else if (entity.getPositionName() == null || entity.getPositionName().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工職位");
		} else if (entity.getDepartmentName() == null || entity.getDepartmentName().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工部門");
		} else if (entity.getHireDate() == null) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工入職時間");
		} else if (entity.getGender() == null || entity.getGender().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工性別");
		} else if (entity.getIdentityCard() == null || entity.getIdentityCard().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工身分證");
		} else if (entity.getEmail() == null || entity.getEmail().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工信箱");
		} else if (entity.getPhone() == null || entity.getPhone().length() == 0) {
			empCreRes.setSuccess(false);
			empCreRes.setMessage("請輸入員工電話");
		} else {
			Position position = posSer.findByName(entity.getPositionName());
			if (position == null) {
				empCreRes.setSuccess(false);
				empCreRes.setMessage("請輸入正確職位");
			}
			Department department = depSer.findByName(entity.getDepartmentName());
			if (department == null) {
				empCreRes.setSuccess(false);
				empCreRes.setMessage("請輸入正確部門");
			}
			Status status = staSer.findById(1);
			String password = "1234";
			String encode = pwdEncoder.encode(password);
			employee.setEmployeeName(entity.getEmployeeName());
			employee.setPassword(encode);
			employee.setDepartment(department);
			employee.setPosition(position);
			employee.setHireDate(entity.getHireDate());
			employee.setStatus(status);
			Employee empbean = empService.employeeCreate(employee);
			Integer id = empbean.getEmployeeId();
			System.out.println(id);
			empDet.setEmployeeId(id);
			empDet.setEmail(entity.getEmail());
			empDet.setGender(entity.getGender());
			empDet.setIdentityCard(entity.getIdentityCard());
			empDet.setPhone(entity.getPhone());
			empDetSer.empDetCreate(empDet);
			empCreRes.setSuccess(true);
			empCreRes.setMessage("員工新增成功");
		}

		return empCreRes;
	}

}
