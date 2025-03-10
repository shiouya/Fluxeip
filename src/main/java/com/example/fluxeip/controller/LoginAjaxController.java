package com.example.fluxeip.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.LoginRequest;
import com.example.fluxeip.dto.LoginResponse;
import com.example.fluxeip.jwt.JsonWebTokenUtility;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.service.EmployeeService;

@CrossOrigin
@RestController
public class LoginAjaxController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;

	@PostMapping("/secure/ajax/login")
	public LoginResponse login(@RequestBody LoginRequest entity) {
		LoginResponse response = new LoginResponse();

		String userId = entity.getUserId();
		String password = entity.getPassword();
		if (userId == null || userId.length() == 0 || password == null || password.length() == 0) {
			response.setSuccess(false);
			response.setMessage("請輸入帳號與密碼以便執行登入");
			return response;
		}

		Employee bean = employeeService.login(Integer.valueOf(userId), password);
		if (bean == null) {
			response.setSuccess(false);
			response.setMessage("登入失敗");
		} else {
			response.setSuccess(true);
			response.setMessage("登入成功");
			// JWT start
			JSONObject user = new JSONObject().put("id", bean.getEmployeeId()).put("name", bean.getEmployeeName())
					.put("department", bean.getDepartment()).put("position", bean.getPosition());
			String token = jsonWebTokenUtility.createToken(user.toString());
			response.setToken(token);
			response.setEmployeeId(bean.getEmployeeId());
			System.out.println("token" + token);

		}
		return response;
	}


}
