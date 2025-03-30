package com.example.fluxeip.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.FieldWorkRecord;

@Repository
public interface FieldWorkRecordRepository extends JpaRepository<FieldWorkRecord, Integer> {

    // 根據員工ID查詢外勤記錄
    List<FieldWorkRecord> findByEmployee_EmployeeId(Integer employeeId);

    // 根據日期範圍查詢
    List<FieldWorkRecord> findByFieldWorkDateBetween(LocalDate startDate, LocalDate endDate);

	Optional<FieldWorkRecord> findByEmployee_EmployeeIdAndFieldWorkDate(Integer employeeId, LocalDate today);
}