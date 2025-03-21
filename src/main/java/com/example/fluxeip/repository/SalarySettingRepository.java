package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.SalarySetting;
import java.util.List;


@Repository
public interface SalarySettingRepository extends JpaRepository<SalarySetting, Integer>{
	SalarySetting findByEmployee(Employee employee);
	
    // 檢查某位員工是否已有薪資設定
    boolean existsByEmployeeEmployeeId(Integer employeeId);
}
