package com.example.fluxeip.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.TaskassignRequest;
import com.example.fluxeip.dto.WorkTaskassignResponse;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.TaskassignRepository;
import com.example.fluxeip.repository.WorkProgessRepository;
import com.example.fluxeip.service.EmployeeService;
import com.example.fluxeip.service.StatusService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin
@RestController
public class TaskassignController {
	
	@Autowired
	private StatusService staSer;
	
	@Autowired
	private EmployeeRepository empRep;

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
	
	@PutMapping("/taskassign/{id}")
	public boolean putMethodName(@PathVariable String id, @RequestBody TaskassignRequest entity) {
		Optional<Taskassign> existingTaskassign = taskRep.findById(Integer.valueOf(id));
	    
	    if (existingTaskassign.isPresent()) {
	        Taskassign taskassign = existingTaskassign.get();
	        taskassign.setTaskName(entity.getTaskName()); 
	        taskassign.setTaskContent(entity.getTaskContent()); 
	        taskassign.setCreateDate(entity.getCreateDate());
	        taskassign.setExpectedFinishDate(entity.getExpectedFinishDate());
	        taskassign.setFinishDate(entity.getFinishDate());
	        Employee employee = empRep.findByEmployeeName(entity.getEmployee());
	        taskassign.setAssign(employee);
	        Status status = staSer.findByName(entity.getStatus());
	        taskassign.setStatus(status);
	        // 更新資料庫中的 taskassign
	        taskRep.save(taskassign); // 儲存更新後的資料
	        
	        return true; // 返回成功
	    }
	    return false;
	}

}
