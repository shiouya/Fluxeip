package com.example.fluxeip.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@ManyToMany(mappedBy = "roles")
	private List<Employee> employee = new LinkedList<Employee>();

	public Roles() {
	}

}
