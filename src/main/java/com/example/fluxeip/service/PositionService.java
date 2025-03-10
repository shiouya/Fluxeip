package com.example.fluxeip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.Position;
import com.example.fluxeip.repository.PositionRepository;

@Service
public class PositionService {

	@Autowired
	private PositionRepository posRep;

	public Position findByName(String positionName) {
		Optional<Position> position = posRep.findByPositionName(positionName);
		if (position != null) {
			return position.get();
		} else {
			return null;
		}
	}

	public List<Position> findAll() {
		List<Position> positions = posRep.findAll();
		return positions;
	}

}
