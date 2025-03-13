package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.ShiftType;

@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, Integer>{

}
