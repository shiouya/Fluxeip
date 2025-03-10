package com.example.fluxeip.model;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

//@Entity
//@Table(name = "taskassign")
public class Taskassign {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer taskId;

	@ManyToOne
	@JoinColumn(name = "workprogress_id")
	private WorkProgess workprogess;

	private String taskName;

	private String taskContent;

	@ManyToOne
	@JoinColumn(name = "assign_id")
	private Employee assign;

	@ManyToOne
	@JoinColumn(name = "reveiew_id")
	private Employee reveiew;

	private Date createDate;

	private Date expectedFinishDate;

	private Date finishDate;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;

	public Taskassign() {
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public WorkProgess getWorkprogess() {
		return workprogess;
	}

	public void setWorkprogess(WorkProgess workprogess) {
		this.workprogess = workprogess;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskContent() {
		return taskContent;
	}

	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
	}

	public Employee getAssign() {
		return assign;
	}

	public void setAssign(Employee assign) {
		this.assign = assign;
	}

	public Employee getReveiew() {
		return reveiew;
	}

	public void setReveiew(Employee reveiew) {
		this.reveiew = reveiew;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getExpectedFinishDate() {
		return expectedFinishDate;
	}

	public void setExpectedFinishDate(Date expectedFinishDate) {
		this.expectedFinishDate = expectedFinishDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
