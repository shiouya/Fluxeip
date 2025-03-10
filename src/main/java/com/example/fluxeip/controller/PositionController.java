package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.Position;
import com.example.fluxeip.service.PositionService;

@RestController
public class PositionController {

	@Autowired
	private PositionService posSer;

	@GetMapping("/position/find")
	public List<Position> positionFindAll() {
		List<Position> positions = posSer.findAll();
		return positions;
	}

}
