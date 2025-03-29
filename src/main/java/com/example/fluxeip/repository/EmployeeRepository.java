package com.example.fluxeip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.model.Status;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>,EmployeeRepositoryCustom{

	Employee findByEmployeeName(String employeeName);
	
	List<Employee> findByDepartmentAndStatus(Department department,Status status);

	Page<Employee> findByStatus(Status status, Pageable pageable);

	// 根據部門和職位進行查詢
	Page<Employee> findByDepartmentAndPositionAndStatus(Department department, Position position, Status status,
			Pageable pageable);

	// 根據部門進行查詢
	Page<Employee> findByDepartmentAndStatus(Department department, Status status, Pageable pageable);

	// 根據職位進行查詢
	Page<Employee> findByPositionAndStatus(Position position, Status status, Pageable pageable);

	long countByStatus(Status status);

	// 根據部門和職位查詢符合條件的員工總數
	long countByDepartmentAndPositionAndStatus(Department department, Position position, Status status);

	// 根據部門查詢符合條件的員工總數
	long countByDepartmentAndStatus(Department department, Status status);

	// 根據職位查詢符合條件的員工總數
	long countByPositionAndStatus(Position position, Status status);
	
	List<Employee> findByDepartmentDepartmentId(Integer departmentId);

	Optional<Employee> findByPositionAndDepartment(Position position, Department department); 
	
	Optional<Employee> findTopByPositionAndDepartmentAndStatus(Position position, Department department,Status status);
	
	Optional<Employee> findTopByPosition(Position position);


}
