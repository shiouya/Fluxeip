package com.example.fluxeip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.EmployeeDetail;

public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Integer> {
	
	Optional<EmployeeDetail> findByEmail(String email);
	Optional<EmployeeDetail> findByIdentityCard(String identityCard);
	Optional<EmployeeDetail> findByPhone(String phone);

}
