package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.SalaryBonus;

@Repository
public interface SalaryBonusRepository extends JpaRepository<SalaryBonus, Integer>{
    
	List<SalaryBonus> findByIsActiveTrue();

    SalaryBonus findBySalaryBonusIdAndIsActiveTrue(Integer salaryBonusId);
    
    List<SalaryBonus> findByIsActiveTrueOrderByBonusTypeAsc();
}
