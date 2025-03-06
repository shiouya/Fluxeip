package com.example.Fluxeip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Fluxeip.model.Position;

public interface PositionRepository extends JpaRepository<Position, Integer> {

	Optional<Position> findByPositionName(String name);

}
