package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.WorkProgessRepository;

@CrossOrigin
@RestController
public class WorkProgressController {

	@Autowired
	private WorkProgessRepository workProRep;


	@GetMapping("/workProgress/all")
	public List<WorkProgess> getWorkProgress() {
		List<WorkProgess> all = workProRep.findAll();
		return all;
	}

	@PostMapping("/workProgress/create")
	public String createWorkProgress(@RequestBody String entity) {
		// TODO: process POST request

		return entity;
	}

}
