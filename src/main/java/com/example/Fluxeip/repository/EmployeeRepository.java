package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
