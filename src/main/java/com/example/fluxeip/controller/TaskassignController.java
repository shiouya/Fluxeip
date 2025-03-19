package com.example.fluxeip.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.WorkTaskassignResponse;
import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.TaskassignRepository;
import com.example.fluxeip.repository.WorkProgessRepository;

@CrossOrigin
@RestController
public class TaskassignController {

	@Autowired
	private WorkProgessRepository workRep;

	@Autowired
	private TaskassignRepository taskRep;

	@GetMapping("/work/taskassign/{id}")
	public WorkTaskassignResponse getWorkTaskassign(@PathVariable Integer id) {
		WorkTaskassignResponse workTaskassignResponse = new WorkTaskassignResponse();
		Optional<WorkProgess> work = workRep.findById(id);
		WorkProgess workProgess = new WorkProgess();
		if (work != null) {
			workProgess = work.get();
		}
		List<Taskassign> Taskassigns = taskRep.findByWorkprogess(workProgess);
		workTaskassignResponse.setTaskassign(Taskassigns);
		workTaskassignResponse.setWorkprogress(workProgess);
		return workTaskassignResponse;
	}

	@GetMapping("/taskassign/{id}")
	public Taskassign getTaskassign(@PathVariable Integer id) {
		Taskassign taskassign = new Taskassign();
		Optional<Taskassign> task = taskRep.findById(id);
		if (task != null) {
			taskassign = task.get();
		}
		return taskassign;
	}

}
