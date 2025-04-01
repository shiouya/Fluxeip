package com.example.fluxeip.repository;

import com.example.fluxeip.model.PersonalCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalCalendarRepository extends JpaRepository<PersonalCalendar, Integer> {

    // 根據員工ID查詢事件
    List<PersonalCalendar> findByEmployeeId(String employeeId);
}