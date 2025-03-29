package com.example.fluxeip.controller;

import java.util.LinkedList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.EmployeeCreateRequest;
import com.example.fluxeip.dto.EmployeeCreateResponse;
import com.example.fluxeip.dto.SalaryDefaultSetting;
import com.example.fluxeip.jwt.JsonWebTokenUtility;
import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeDetail;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.model.Roles;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.RolesRepository;
import com.example.fluxeip.service.DepartmentService;
import com.example.fluxeip.service.EmailService;
import com.example.fluxeip.service.EmployeeDetailService;
import com.example.fluxeip.service.EmployeeService;
import com.example.fluxeip.service.PositionService;
import com.example.fluxeip.service.SalaryService;
import com.example.fluxeip.service.StatusService;

import jakarta.mail.MessagingException;


@RestController
public class UserController {

	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;

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

	@Autowired
	private EmployeeRepository empRep;
	
	@Autowired
	private SalaryService salaryService;

	@Autowired
	private RolesRepository rolesRep;
	
	@Autowired
	private EmailService emailSer;

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
		} else if(empDetSer.isEmailExist(entity.getEmail())){
			empCreRes.setSuccess(false);
			empCreRes.setMessage("信箱已有人使用");
		} else if(empDetSer.isIdentityCardExist(entity.getIdentityCard())){
			empCreRes.setSuccess(false);
			empCreRes.setMessage("身分證已有人使用");
		} else if(empDetSer.isPhoneExist(entity.getPhone())){
			empCreRes.setSuccess(false);
			empCreRes.setMessage("電話已有人使用");
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
			LinkedList<Roles> rloes = new LinkedList<Roles>();
			Roles roles1 = rolesRep.findByRoleName("最高管理員");
			Roles roles2 = rolesRep.findByRoleName("次等管理員");
			Roles roles3 = rolesRep.findByRoleName("行政主管");
			Roles roles4 = rolesRep.findByRoleName("人資主管");
			Roles roles5 = rolesRep.findByRoleName("業務主管");
			Roles roles6 = rolesRep.findByRoleName("技術主管");
			Roles roles7 = rolesRep.findByRoleName("員工");
			if (entity.getDepartmentName().equals("行政部") && entity.getPositionName().equals("經理")) {
				employee.getRoles().add(roles3);
			} else if (entity.getDepartmentName().equals("人資部") && entity.getPositionName().equals("經理")) {
				employee.getRoles().add(roles4);
			} else if (entity.getDepartmentName().equals("業務部") && entity.getPositionName().equals("經理")) {
				employee.getRoles().add(roles5);
			} else if (entity.getDepartmentName().equals("技術部") && entity.getPositionName().equals("經理")) {
				employee.getRoles().add(roles6);
			} else if (entity.getPositionName().equals("組長") || entity.getPositionName().equals("員工")) {
				employee.getRoles().add(roles7);
			} else if (entity.getDepartmentName().equals("總經理部") && entity.getPositionName().equals("老闆")) {
				employee.getRoles().add(roles1);
			} else if (entity.getDepartmentName().equals("總經理部") && entity.getPositionName().equals("總經理")) {
				employee.getRoles().add(roles2);
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
			EmployeeDetail empDetCreate = empDetSer.empDetCreate(empDet);

			try {
				emailSer.sendNewEmployee(entity.getEmail(), id);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			if (empbean != null && empDetCreate != null) {

				try {
					SalaryDefaultSetting salarySetting = new SalaryDefaultSetting();
					salarySetting.setEmployeeID(id);
					salarySetting.setMonthlySalary(0);
					salarySetting.setHourlyWage(190);
					salaryService.settingDefaultSalary(salarySetting);
					empCreRes.setSuccess(true);
					empCreRes.setMessage("ID :" + id + " 姓名 :" + entity.getEmployeeName() + " 新增成功");

				} catch (Exception e) {
					e.getMessage();
					empCreRes.setSuccess(false);
					empCreRes.setMessage("員工新增成功，薪資設定失敗");
				}

			}
		}
		return empCreRes;
	}
	
	@GetMapping("/check/email/{email}")
	public Boolean checkEmail(@PathVariable String email) {
		boolean emailExist = empDetSer.isEmailExist(email);
		if(emailExist) {
			return true;
		}else {
		return false;
		}
	}
	@GetMapping("/check/identityCard/{identityCard}")
	public Boolean checkIdentityCard(@PathVariable String identityCard) {
		boolean identityCardExist = empDetSer.isIdentityCardExist(identityCard);
		if(identityCardExist) {
			return true;
		}else {
			return false;
		}
	}
	@GetMapping("/check/phone/{phone}")
	public Boolean checkPhone(@PathVariable String phone) {
		boolean phoneExist = empDetSer.isPhoneExist(phone);
		if(phoneExist) {
			return true;
		}else {
			return false;
		}
	}

	@PostMapping("/password/update")
	public boolean passwordUpdate(@RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword,@RequestHeader("Authorization") String authorization) {
		int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return false;
        }
        Employee employee = empService.find(employeeId);
		String dbEncodedPasswprd = employee.getPassword();
		if (!pwdEncoder.matches(oldPassword, dbEncodedPasswprd)) {
			return false;
		} else {
			String encode = pwdEncoder.encode(newPassword);
			employee.setPassword(encode);
			empRep.save(employee);
			return true;
	}

	}

	private int extractEmployeeIdFromToken(String authorization) {
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return -1;
		}

		String token = authorization.substring(7);
		String userJsonString = jsonWebTokenUtility.validateToken(token);

		if (userJsonString == null || userJsonString.isEmpty()) {
			return -1;
		}

		try {
			JSONObject userJson = new JSONObject(userJsonString);
			return userJson.getInt("id");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
