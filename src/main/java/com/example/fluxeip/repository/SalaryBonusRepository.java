package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.SalaryBonus;

@Repository
public interface SalaryBonusRepository extends JpaRepository<SalaryBonus, Integer>{
    
	List<SalaryBonus> findByIsActiveTrue();

    SalaryBonus findBySalaryBonusIdAndIsActiveTrue(Integer salaryBonusId);
    
    List<SalaryBonus> findByIsActiveTrueOrderByBonusTypeAsc();
    
    @Query("""
    	    SELECT COUNT(sb) > 0 FROM SalaryBonus sb 
    	    JOIN sb.salaryDetails sd 
    	    WHERE sb.salaryBonusId = :bonusId
    	""")
    	boolean existsInSalaryDetail(@Param("bonusId") Integer bonusId);
}
