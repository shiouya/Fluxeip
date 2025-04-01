package com.example.fluxeip.repository;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.DepartmentCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartmentCalendarRepository extends JpaRepository<DepartmentCalendar, Integer> {

    // 根據部門 ID 查詢所有事件
	List<DepartmentCalendar> findByDepartment_DepartmentId(Integer departmentId);
	

    // 根據部門對象查詢所有事件
    List<DepartmentCalendar> findByDepartment(Department department);
}