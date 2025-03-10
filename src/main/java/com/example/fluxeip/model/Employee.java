package com.example.fluxeip.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@ToString
@Entity
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id")
	private Integer employeeId;

	@Column(name = "employee_name")
	private String employeeName;

	@Column(name = "password")
	private String password;

	@ManyToOne
	@JoinColumn(name = "position_id")
	private Position position;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "hire_date")
	private Date hireDate;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;
	
	@OneToOne(mappedBy = "employee")
	private EmployeeDetail employeeDetail;
//
//	@OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
//	private List<WorkProgess> workprogess = new LinkedList<WorkProgess>();
//
//	@OneToMany(mappedBy = "assign", cascade = CascadeType.ALL)
//	private List<Taskassign> assign = new LinkedList<Taskassign>();
//
//	@OneToMany(mappedBy = "reveiew", cascade = CascadeType.ALL)
//	private List<Taskassign> reveiew = new LinkedList<Taskassign>();
//
//	@ManyToMany(mappedBy = "employee")
//	private List<Roles> roles = new LinkedList<Roles>();

	public Employee() {
	}


//	public List<WorkProgess> getWorkprogess() {
//		return workprogess;
//	}
//
//	public void setWorkprogess(List<WorkProgess> workprogess) {
//		this.workprogess = workprogess;
//	}

}
