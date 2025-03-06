package com.example.Fluxeip.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

//@Entity
//@Table(name = "roles")
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer roleId;

	private String roleName;

	@OneToMany(mappedBy = "roles")
	private List<Permissions> permissions = new LinkedList<Permissions>();

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "employee_roles",
	joinColumns={@JoinColumn(name="role_id",referencedColumnName="role_id")},
	inverseJoinColumns={@JoinColumn(name="employee_id",referencedColumnName = "employee_id")})
	private List<Employee> employee = new LinkedList<Employee>();

	public Roles() {
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<Permissions> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}

}
