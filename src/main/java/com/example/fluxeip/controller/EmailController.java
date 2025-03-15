package com.example.fluxeip.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.jwt.JsonWebTokenUtility;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeDetail;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.service.EmailService;
import com.example.fluxeip.service.EmployeeDetailService;
import com.example.fluxeip.service.EmployeeService;

@RestController
public class EmailController {
	
	@Autowired
	private EmployeeRepository empRep;
	
	@Autowired
	private PasswordEncoder pwdEncoder;
	
	@Autowired
    private EmployeeService employeeService;
	
	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;
	
	@Autowired
    private EmailService emailService;
	
	@Autowired
	private EmployeeDetailService empDetSer;

    @PostMapping("/forgot/password")
    public boolean forgotPassword(@RequestParam String email,
    		@RequestParam Integer id,@RequestParam String name) {
    	Employee employee = employeeService.find(id);
    	EmployeeDetail empDet = empDetSer.empDetByIdFind(id);
    	if(employee == null || empDet == null) {
    		return false;
    	}else if(!employee.getEmployeeName().equals(name) || !empDet.getEmail().equals(email)){
    		return false;
    	}else {
    		JSONObject user = new JSONObject()
				.put("id", id);
    	String token = jsonWebTokenUtility.createToken(user.toString());
        // 發送驗證郵件
        emailService.sendVerificationEmail(email, token);
        return true;
    	}
    	
    }
    
    @PostMapping("/reset/password")
    public boolean resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        // 驗證 Token 是否過期
    	String empJsonString = jsonWebTokenUtility.validateToken(token);
    	if (empJsonString == null || empJsonString.isEmpty()) {
			return false;
		}
    	JSONObject empJson = new JSONObject(empJsonString);
    	int id = empJson.getInt("id");
    	Employee employee = employeeService.find(id);
    	
    	String encode = pwdEncoder.encode(newPassword);
		employee.setPassword(encode);
		empRep.save(employee);

        return true;
    }

}
