package com.example.fluxeip.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.example.fluxeip.service.StatusService;



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

	@GetMapping("/work/taskassign/{id}/{status}")
	public List<Taskassign> getWorkTaskassignByStatus(@PathVariable Integer id, @PathVariable String status) {
		Optional<WorkProgess> work = workRep.findById(id);
		WorkProgess workProgess = new WorkProgess();
		if (work != null) {
			workProgess = work.get();
		}
		Status statu = staSer.findByName(status);
		List<Taskassign> Taskassigns = taskRep.findByWorkprogessAndStatus(workProgess, statu);
		return Taskassigns;
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
	public boolean updateTaskassign(@PathVariable String id, @RequestBody TaskassignRequest entity) {
		Optional<Taskassign> existingTaskassign = taskRep.findById(Integer.valueOf(id));
	    
	    if (existingTaskassign.isPresent()) {
	        Taskassign taskassign = existingTaskassign.get();
	        taskassign.setTaskName(entity.getTaskName()); 
	        taskassign.setTaskContent(entity.getTaskContent()); 
	        taskassign.setCreateDate(entity.getCreateDate());
	        taskassign.setExpectedFinishDate(entity.getExpectedFinishDate());
	        Employee employee = empRep.findByEmployeeName(entity.getEmployee());
	        taskassign.setAssign(employee);
	        // 更新資料庫中的 taskassign
	        taskRep.save(taskassign); // 儲存更新後的資料
	        
	        return true; // 返回成功
	    }
	    return false;
	}

	@PostMapping("/taskassign/create/{workid}")
	public boolean createTaskassign(@PathVariable String workid, @RequestBody TaskassignRequest entity) {
		Optional<WorkProgess> work = workRep.findById(Integer.valueOf(workid));
		WorkProgess workProgess = new WorkProgess();
		if (work != null) {
			workProgess = work.get();
		}
		Taskassign taskassign = new Taskassign();
		taskassign.setWorkprogess(workProgess);
		taskassign.setTaskName(entity.getTaskName());
		taskassign.setTaskContent(entity.getTaskContent());
		Employee assign = empRep.findByEmployeeName(entity.getEmployee());
		Employee review = empRep.findByEmployeeName(entity.getReveiew());
		taskassign.setAssign(assign);
		taskassign.setReveiew(review);
		taskassign.setCreateDate(entity.getCreateDate());
		taskassign.setExpectedFinishDate(entity.getExpectedFinishDate());
		Status status = staSer.findByStatusNameAndStatusType("未完成", "工作狀態");
		taskassign.setStatus(status);
		taskRep.save(taskassign); // 儲存更新後的資料
		
		long countByWorkprogess = taskRep.countByWorkprogess(workProgess);
		Status finishStatus = staSer.findByStatusNameAndStatusType("已完成", "工作狀態");
		long countByWorkprogessAndStatus = taskRep.countByWorkprogessAndStatus(workProgess, finishStatus);
		Double progress=(double) countByWorkprogessAndStatus/countByWorkprogess*100;
		double roundedProgress = Math.round(progress * 100.0) / 100.0;
		workProgess.setProgress(roundedProgress);
		workRep.save(workProgess);
		return true;
	}
	
	@GetMapping("/taskassign/emp/{id}")
	public ResponseEntity<List<Taskassign>> getTaskassignByEmp(@PathVariable Integer id) {
		Optional<Employee> emp = empRep.findById(id);
		Employee employee=null;
		if(emp.isPresent()) {
			employee = emp.get();
		}
		List<Taskassign> taskassign = taskRep.findByAssign(employee);
		
		return ResponseEntity.ok(taskassign);
	}
	
	@GetMapping("/taskassign/emp/{id}/{status}")
	public ResponseEntity<List<Taskassign>> getTaskassignByEmpAndStatus(@PathVariable Integer id,@PathVariable String status) {
		Optional<Employee> emp = empRep.findById(id);
		Employee employee=null;
		if(emp.isPresent()) {
			employee = emp.get();
		}
		Status statu = staSer.findByName(status);
		List<Taskassign> taskassign = taskRep.findByAssignAndStatus(employee,statu);
		
		return ResponseEntity.ok(taskassign);
	}
	
	@PutMapping("/taskassign/update/{id}/{status}")
	public boolean reviewTaskassign(@PathVariable Integer id,@PathVariable String status) {
		Optional<Taskassign> task = taskRep.findById(id);
		System.out.println(status + "21111111111111111111111111111111111111111111111111111111");
		Taskassign taskassign=null;
		if(task.isPresent()) {
			taskassign = task.get();
		}
		Status statu = staSer.findByStatusNameAndStatusType(status, "工作狀態");
		Status finishStatus = staSer.findByStatusNameAndStatusType("已完成", "工作狀態");
		taskassign.setStatus(statu);
		WorkProgess workprogess = taskassign.getWorkprogess();
		if (status.equals("已完成")) {
			LocalDate today = LocalDate.now();
			taskassign.setFinishDate(today);
		}
		taskRep.save(taskassign);
		long countByWorkprogess = taskRep.countByWorkprogess(workprogess);
		long countByWorkprogessAndStatus = taskRep.countByWorkprogessAndStatus(workprogess, finishStatus);
		Double progress=(double) countByWorkprogessAndStatus/countByWorkprogess*100;
		double roundedProgress = Math.round(progress * 100.0) / 100.0;
		workprogess.setProgress(roundedProgress);
		WorkProgess save = workRep.save(workprogess);
		
		return true;
	}
	
	@DeleteMapping("/taskassign/{id}")
	public boolean taskassignDelete(@PathVariable Integer id) {
		Optional<Taskassign> task = taskRep.findById(id);
		Taskassign taskassign=null;
		if(task.isPresent()) {
			taskassign = task.get();
		}
		taskRep.deleteById(id);
		
		WorkProgess workprogess = taskassign.getWorkprogess();
		long countByWorkprogess = taskRep.countByWorkprogess(workprogess);
		Status finishStatus = staSer.findByStatusNameAndStatusType("已完成", "工作狀態");
		long countByWorkprogessAndStatus = taskRep.countByWorkprogessAndStatus(workprogess, finishStatus);
		Double progress=(double) countByWorkprogessAndStatus/countByWorkprogess*100;
		double roundedProgress = Math.round(progress * 100.0) / 100.0;
		workprogess.setProgress(roundedProgress);
		WorkProgess save = workRep.save(workprogess);
		return true;
	}

}
