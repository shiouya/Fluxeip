package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/position/find/{departmentName}")
	public List<Position> positionFindAll(@PathVariable String departmentName) {
		List<Position> positions = posSer.findAll();
		System.out.println(departmentName);
		if (departmentName.equals("總經理部")) {
			Position position1 = posSer.findByName("經理");
			Position position2 = posSer.findByName("組長");
			Position position3 = posSer.findByName("員工");
			positions.remove(position1);
			positions.remove(position2);
			positions.remove(position3);
			return positions;
		}
		Position position1 = posSer.findByName("老闆");
		Position position2 = posSer.findByName("總經理");
		positions.remove(position1);
		positions.remove(position2);
	return positions;
}

}
