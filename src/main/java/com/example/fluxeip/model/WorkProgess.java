package com.example.fluxeip.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workprogress")
public class WorkProgess {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workprogress_id")
	private Integer workprogressId;

	@Column(name = "work_name")
	private String workName;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "expected_finish_date")
	private Date expectedFinishDate;

	@Column(name = "finish_date")
	private LocalDate finishDate;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "supervisor_id")
	private Employee supervisor;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;

	@OneToMany(mappedBy = "workprogess", cascade = CascadeType.ALL)
	private List<Taskassign> taskassign = new LinkedList<Taskassign>();

	private Double progress;

	public WorkProgess() {
	}

}
