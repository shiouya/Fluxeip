package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.SalaryDetail;
import java.util.List;


@Repository
public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, Integer>{
List<SalaryDetail> findByEmployee(Employee employee);
List<SalaryDetail> findByYearMonthAndEmployee(String yearMonth,Employee employee);
}
