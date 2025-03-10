package com.example.fluxeip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Position;

public interface PositionRepository extends JpaRepository<Position, Integer> {

	Optional<Position> findByPositionName(String name);

}
