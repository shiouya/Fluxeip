package com.example.fluxeip.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "employee_roles", joinColumns = {
			@JoinColumn(name = "employee_id", referencedColumnName = "employee_id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "role_id") })
	private List<Roles> roles = new LinkedList<Roles>();
//	@OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
//	private List<WorkProgess> workprogess = new LinkedList<WorkProgess>();
//
//	@OneToMany(mappedBy = "assign", cascade = CascadeType.ALL)
//	private List<Taskassign> assign = new LinkedList<Taskassign>();
//
//	@OneToMany(mappedBy = "reveiew", cascade = CascadeType.ALL)
//	private List<Taskassign> reveiew = new LinkedList<Taskassign>();
//

	public Employee() {
	}
	
    public Employee(Integer employeeId) {
        this.employeeId = employeeId;
    }

}
