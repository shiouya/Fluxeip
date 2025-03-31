package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.SalaryBonus;
import com.example.fluxeip.model.ShiftType;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, Integer>{

	List<ShiftType> findByIsActiveTrue();
	List<ShiftType> findByIsActiveTrueOrderByDepartment();

}
