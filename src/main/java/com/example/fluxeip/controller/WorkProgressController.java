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

import com.example.fluxeip.dto.WorkTaskCreateRequest;
import com.example.fluxeip.dto.WorkUpdateRequest;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.TaskassignRepository;
import com.example.fluxeip.repository.WorkProgessRepository;
import com.example.fluxeip.service.StatusService;

@CrossOrigin
@RestController
public class WorkProgressController {
	
	@Autowired
	private StatusService statusSer;

	@Autowired
	private EmployeeRepository empRep;

	@Autowired
	private StatusService staSer;

	@Autowired
	private WorkProgessRepository workProRep;

	@Autowired
	private TaskassignRepository taskRep;

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
		Status statusName = staSer.findByName(statusN);
		
		List<WorkProgess> WorkProgessFindByStatus = workProRep.findByStatus(statusName);
		return WorkProgessFindByStatus;
	}
	
	@GetMapping("/workProgress/find/{statusN}/{name}")
	public List<WorkProgess> getWorkProgressByStatusAndName(@PathVariable String statusN,@PathVariable String name) {
		Status status = staSer.findByName(statusN);
		
		List<WorkProgess> WorkProgessFindByStatus = workProRep.findByNameAndStatus(name,status);
		return WorkProgessFindByStatus;
	}
	

	@PostMapping("/workProgress/create")
	public boolean createWorkProgress(@RequestBody WorkTaskCreateRequest entity) {
		WorkProgess workProgess = new WorkProgess();
		workProgess.setWorkName(entity.getWorkName());
		workProgess.setCreateDate(entity.getCreateDate());
		workProgess.setExpectedFinishDate(entity.getExpectedFinishdate());
		workProgess.setProgress(0.0);
		Status status = statusSer.findByName("未完成");
		workProgess.setStatus(status);
		Optional<Employee> emp = empRep.findById(entity.getSupervisorId());
		if (emp.isPresent()) {
			Employee employee = emp.get();
			workProgess.setSupervisor(employee);
		}
		WorkProgess work = workProRep.save(workProgess);
		entity.getTaskassigns().forEach(task -> {
			Taskassign taskassign = new Taskassign();
			taskassign.setWorkprogess(work);
			taskassign.setTaskName(task.getTaskName());
			taskassign.setTaskContent(task.getTaskContent());
			Employee employee = null;
			taskassign.setAssign(empRep.findByEmployeeName(task.getEmployee()));
			if (emp.isPresent()) {
				employee = emp.get();
				taskassign.setReveiew(employee);
			}
			taskassign.setCreateDate(task.getCreateDate());
			taskassign.setExpectedFinishDate(task.getExpectedFinishDate());
			taskassign.setStatus(status);
			taskRep.save(taskassign);
		});

		return true;
	}
	
	@PostMapping("/workProgress/update")
	public boolean updateWorkProgress(@RequestBody WorkUpdateRequest entity) {
		Optional<WorkProgess> workProgress = workProRep.findById(entity.getWorkId());
		WorkProgess work = workProgress.get();
		work.setWorkName(entity.getWorkName());
		work.setCreateDate(entity.getCreateDate());
		work.setExpectedFinishDate(entity.getExpectedFinishdate());
		work.setFinishDate(entity.getFinishdate());
		Status status = statusSer.findByName(entity.getStatus());
		work.setStatus(status);
		entity.getTaskassigns().forEach(task -> {
			if (task.getTaskId() == null) {
				Taskassign taskassign = new Taskassign();
				taskassign.setWorkprogess(work);
				taskassign.setTaskName(task.getTaskName());
				taskassign.setTaskContent(task.getTaskContent());
				taskassign.setAssign(empRep.findByEmployeeName(task.getEmployee()));
				Optional<Employee> emp = empRep.findById(task.getReveiew());
				taskassign.setReveiew(emp.get());
				taskassign.setCreateDate(task.getCreateDate());
				taskassign.setExpectedFinishDate(task.getExpectedFinishDate());
				Status statuss = statusSer.findByName(task.getStatus());
				taskassign.setStatus(statuss);
				taskRep.save(taskassign);
			} else {
				Taskassign taskassign = taskRep.findById(task.getTaskId()).get();
				taskassign.setTaskName(task.getTaskName());
				taskassign.setTaskContent(task.getTaskContent());
				taskassign.setAssign(empRep.findByEmployeeName(task.getEmployee()));
				taskassign.setCreateDate(task.getCreateDate());
				taskassign.setExpectedFinishDate(task.getExpectedFinishDate());
				Status statuss = statusSer.findByName(task.getStatus());
				taskassign.setStatus(statuss);
				taskRep.save(taskassign);
			}
		});
		long countByWorkprogess = taskRep.countByWorkprogess(work);
		Status finishStatus = staSer.findByName("已完成");
		long countByWorkprogessAndStatus = taskRep.countByWorkprogessAndStatus(work, finishStatus);
		Double progress = (double) countByWorkprogessAndStatus / countByWorkprogess * 100;
		double roundedProgress = Math.round(progress * 100.0) / 100.0;
		work.setProgress(roundedProgress);
		WorkProgess save = workProRep.save(work);
		
		return true;
	}

}
