package com.example.fluxeip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.EmployeeRepository;


@Service
@Transactional
public class EmployeeService {
	
	@Autowired
	private StatusService staSer;

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

	public Page<Employee> getEmployees(Status status, int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		return employeeRepository.findByStatus(status, pageRequest);
	}

	public Page<Employee> getEmployeesByDepartmentAndPosition(Department department, Position position, Status status,
			int page,
			int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return employeeRepository.findByDepartmentAndPositionAndStatus(department, position, status, pageable);
	}

	// 根據部門進行查詢並分頁
	public Page<Employee> getEmployeesByDepartment(Department department, Status status, int page, int size) {
		PageRequest request = PageRequest.of(page, size);
		return employeeRepository.findByDepartmentAndStatus(department, status, request);
	}

	// 根據職位進行查詢並分頁
	public Page<Employee> getEmployeesByPosition(Position position, Status status, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return employeeRepository.findByPositionAndStatus(position, status, pageable);
	}

	public List<Employee> employeeFindByDepartment(Department dep) {
		Status status = staSer.findByName("在職");
		return employeeRepository.findByDepartmentAndStatus(dep,status);
	}
	
	
	// 用criteriaquery找
	public Page<Employee> getEmployeesByDepartmentAndPosition(Department department, Position position, Status status, Pageable pageable) {
        return employeeRepository.findEmployeesByDepartmentAndPosition(department, position, status, pageable);
    }

}
