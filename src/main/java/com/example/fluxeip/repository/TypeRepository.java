package com.example.fluxeip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {

	Optional<Type> findByTypeName(String exceptionMessage);
	
	List<Type> findByCategory(String category);
}