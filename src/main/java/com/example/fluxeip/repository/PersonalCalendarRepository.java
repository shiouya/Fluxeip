package com.example.fluxeip.repository;  // 修改包名

import com.example.fluxeip.model.PersonalCalendar;  // 引用新的包
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalCalendarRepository extends JpaRepository<PersonalCalendar, Integer> {
}
