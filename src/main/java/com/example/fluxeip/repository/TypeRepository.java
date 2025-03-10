package com.example.fluxeip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Type;

public interface TypeRepository extends JpaRepository<Type, Integer> {

	Optional<Type> findByTypeName(String exceptionMessage);
}