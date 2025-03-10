package com.example.fluxeip.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

//@Entity
//@Table(name = "permissions")
public class Permissions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer permissionId;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Roles roles;

	@ManyToOne
	@JoinColumn(name = "resources_id")
	private Resources resources;

	private String action;

	public Permissions() {
	}

	public Integer getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Integer permissionId) {
		this.permissionId = permissionId;
	}

	public Roles getRoles() {
		return roles;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	public Resources getResources() {
		return resources;
	}

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
