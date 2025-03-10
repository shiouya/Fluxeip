package com.example.fluxeip.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

//@Entity
//@Table(name = "workprogress")
public class WorkProgess {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer workprogressId;

	private String workName;

	private Date createDate;

	private Date expectedFinishDate;

	private Date finishDate;

	@ManyToOne
	@JoinColumn(name = "supervisor_id")
	private Employee supervisor;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;

	@OneToMany(mappedBy = "workprogess", cascade = CascadeType.ALL)
	private List<Taskassign> taskassign = new LinkedList<Taskassign>();

	public WorkProgess() {
	}

	public Integer getWorkprogressId() {
		return workprogressId;
	}

	public void setWorkprogressId(Integer workprogressId) {
		this.workprogressId = workprogressId;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
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

	public Employee getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Employee supervisor) {
		this.supervisor = supervisor;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Taskassign> getTaskassign() {
		return taskassign;
	}

	public void setTaskassign(List<Taskassign> taskassign) {
		this.taskassign = taskassign;
	}

}
