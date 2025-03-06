package com.example.Fluxeip.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "status")
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Integer statusId;

	@Column(name = "status_name")
	private String statusName;

	@Column(name = "status_type")
	private String statusType;

//	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
//	private List<Employee> employee = new LinkedList<Employee>();
//
//	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
//	private List<WorkProgess> workprogess = new LinkedList<WorkProgess>();

//	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
//	private List<Taskassign> taskassign = new LinkedList<Taskassign>();

	public Status() {
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

//	public List<Taskassign> getTaskassign() {
//		return taskassign;
//	}
//
//	public void setTaskassign(List<Taskassign> taskassign) {
//		this.taskassign = taskassign;
//	}

//	public List<Employee> getEmployee() {
//		return employee;
//	}
//
//	public void setEmployee(List<Employee> employee) {
//		this.employee = employee;
//	}
//	
//	public List<WorkProgess> getWorkprogess() {
//		return workprogess;
//	}
//
//	public void setWorkprogess(List<WorkProgess> workprogess) {
//		this.workprogess = workprogess;
//	}

}
