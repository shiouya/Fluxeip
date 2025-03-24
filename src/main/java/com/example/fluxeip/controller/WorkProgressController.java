package com.example.fluxeip.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.WorkProgessRepository;

@CrossOrigin
@RestController
public class WorkProgressController {
	
	@Autowired
	private StatusRepository statusRep;

	@Autowired
	private WorkProgessRepository workProRep;


	@GetMapping("/workProgress/all")
	public List<WorkProgess> getWorkProgressAll() {
		List<WorkProgess> all = workProRep.findAll();
		return all;
	}
	
	@GetMapping("/workProgress/findname/{name}")
	public List<WorkProgess> getWorkProgressByName(@PathVariable String name) {
		List<WorkProgess> WorkProgessFindByName = workProRep.findByName(name);
		return WorkProgessFindByName;
	}
	
	@GetMapping("/workProgress/findstatus/{statusN}")
	public List<WorkProgess> getWorkProgressByStatus(@PathVariable String statusN) {
		Optional<Status> statusName = statusRep.findByStatusName(statusN);
		Status status = new Status();
		if(statusName!=null) {
			status = statusName.get();
		}
		
		List<WorkProgess> WorkProgessFindByStatus = workProRep.findByStatus(status);
		return WorkProgessFindByStatus;
	}
	
	@GetMapping("/workProgress/find/{statusN}/{name}")
	public List<WorkProgess> getWorkProgressByStatusAndName(@PathVariable String statusN,@PathVariable String name) {
		Optional<Status> statusName = statusRep.findByStatusName(statusN);
		Status status = new Status();
		if(statusName!=null) {
			status = statusName.get();
		}
		
		List<WorkProgess> WorkProgessFindByStatus = workProRep.findByNameAndStatus(name,status);
		return WorkProgessFindByStatus;
	}
	

	@PostMapping("/workProgress/create")
	public String createWorkProgress(@RequestBody String entity) {
		// TODO: process POST request

		return entity;
	}

}
