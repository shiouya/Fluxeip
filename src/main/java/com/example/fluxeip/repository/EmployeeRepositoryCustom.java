package com.example.fluxeip.repository;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {
    Page<Employee> findEmployeesByDepartmentAndPosition(Department department, Position position, Status status, Pageable pageable);
}
