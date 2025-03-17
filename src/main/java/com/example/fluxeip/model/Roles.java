package com.example.fluxeip.model;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Integer roleId;

	@Column(name = "role_name")
	private String roleName;

//	@OneToMany(mappedBy = "roles")
//	private List<Permissions> permissions = new LinkedList<Permissions>();

	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "employee_roles",
	joinColumns={@JoinColumn(name="role_id",referencedColumnName="role_id")},
	inverseJoinColumns={@JoinColumn(name="employee_id",referencedColumnName = "employee_id")})
	private List<Employee> employee = new LinkedList<Employee>();

	public Roles() {
	}

}
