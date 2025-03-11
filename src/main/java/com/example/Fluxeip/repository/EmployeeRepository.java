package com.example.fluxeip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Position;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	// 根據部門和職位進行查詢
	Page<Employee> findByDepartmentAndPosition(Department department, Position position, Pageable pageable);

	// 根據部門進行查詢
	Page<Employee> findByDepartment(Department department, Pageable pageable);

	// 根據職位進行查詢
	Page<Employee> findByPosition(Position position, Pageable pageable);

	// 根據部門和職位查詢符合條件的員工總數
	long countByDepartmentAndPosition(Department department, Position position);

	// 根據部門查詢符合條件的員工總數
	long countByDepartment(Department department);

	// 根據職位查詢符合條件的員工總數
	long countByPosition(Position position);

}
