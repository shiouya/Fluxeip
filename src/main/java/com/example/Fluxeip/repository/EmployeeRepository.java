package com.example.Fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Fluxeip.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
