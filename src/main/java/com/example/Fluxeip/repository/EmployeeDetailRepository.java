package com.example.Fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Fluxeip.model.Employee;
import com.example.Fluxeip.model.EmployeeDetail;

public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Employee> {

}
