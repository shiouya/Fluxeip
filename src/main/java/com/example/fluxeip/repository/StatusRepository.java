package com.example.fluxeip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {

	Optional<Status> findByStatusName(String string);
	Optional<Status> findByStatusNameAndStatusType(String statusName, String statusType);
}
