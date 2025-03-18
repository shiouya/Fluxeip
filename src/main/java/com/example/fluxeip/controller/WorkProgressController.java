package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.WorkProgessRepository;

@RestController
public class WorkProgressController {

	@Autowired
	private WorkProgessRepository workProRep;

	@CrossOrigin
	@GetMapping("/workProgress/all")
	public List<WorkProgess> getWorkProgress() {
		List<WorkProgess> all = workProRep.findAll();
		return all;
	}

}
