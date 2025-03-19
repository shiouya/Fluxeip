package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.SalaryDetail;

@Repository
public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, Integer>{

}
