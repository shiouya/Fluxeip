package com.example.Fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Fluxeip.model.Position;
import com.example.Fluxeip.repository.PositionRepository;

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


}
