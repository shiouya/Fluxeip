package com.example.fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.repository.EmployeeRepository;


@Service
@Transactional
public class EmployeeService {

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Autowired
	private EmployeeRepository employeeReposutory;

	public Employee login(Integer userId, String password) {
		Optional<Employee> employee = employeeReposutory.findById(userId);
		if (employee.isPresent()) {
			if (password != null && password.length() != 0) {
				Employee bean = employee.get();
				String dbEncodedPasswprd = bean.getPassword();
				if (pwdEncoder.matches(password, dbEncodedPasswprd)) {
					return bean;
				}
			}
		}
		return null;
	}

	public Employee employeeCreate(Employee entity) {
		Employee emp = employeeReposutory.save(entity);
		return emp;
	}

}
