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
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.StatusRepository;
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
	private StatusRepository statusRep;

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
	public boolean createWorkProgress(@RequestBody WorkTaskCreateRequest entity) {
		WorkProgess workProgess = new WorkProgess();
		workProgess.setWorkName(entity.getWorkName());
		workProgess.setCreateDate(entity.getCreateDate());
		workProgess.setExpectedFinishDate(entity.getExpectedFinishdate());
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

}
