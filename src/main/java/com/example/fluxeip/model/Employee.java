package com.example.fluxeip.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "position_id")
	private Position position;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "hire_date")
	private Date hireDate;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;

	@JsonIgnore
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
	
    public Employee(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY)
	private List<Attendance> attendance;
//	public List<WorkProgess> getWorkprogess() {
//		return workprogess;
//	}
//
//	public void setWorkprogess(List<WorkProgess> workprogess) {
//		this.workprogess = workprogess;
//	}

}
