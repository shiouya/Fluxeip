package com.example.fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.repository.EmployeeRepository;


@Service
@Transactional
public class EmployeeService {

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee login(Integer userId, String password) {
		Optional<Employee> employee = employeeRepository.findById(userId);
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

	public Employee find(Integer id) {
		Optional<Employee> employee = employeeRepository.findById(id);
		if (employee.isPresent()) {
			Employee bean = employee.get();
			return bean;
		}
		return null;
	}

	public Employee employeeCreate(Employee entity) {
		Employee emp = employeeRepository.save(entity);
		return emp;
	}

	public Page<Employee> getEmployees(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		return employeeRepository.findAll(pageRequest);
	}

	public Page<Employee> getEmployeesByDepartmentAndPosition(Department department, Position position, int page,
			int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return employeeRepository.findByDepartmentAndPosition(department, position, pageable);
	}

	// 根據部門進行查詢並分頁
	public Page<Employee> getEmployeesByDepartment(Department department, int page, int size) {
		PageRequest request = PageRequest.of(page, size);
		return employeeRepository.findByDepartment(department, request);
	}

	// 根據職位進行查詢並分頁
	public Page<Employee> getEmployeesByPosition(Position position, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return employeeRepository.findByPosition(position, pageable);
	}

}
