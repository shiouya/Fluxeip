package com.example.fluxeip.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "taskassign")
public class Taskassign {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private Integer taskId;

	@ManyToOne
	@JoinColumn(name = "workprogress_id")
	private WorkProgess workprogess;

	@Column(name = "task_name")
	private String taskName;

	@Column(name = "task_content")
	private String taskContent;

	@ManyToOne
	@JoinColumn(name = "assign_id")
	private Employee assign;

	@ManyToOne
	@JoinColumn(name = "reveiew_id")
	private Employee reveiew;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "expected_finish_date")
	private Date expectedFinishDate;

	@Column(name = "finish_date")
	private Date finishDate;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;

	public Taskassign() {
	}

}
