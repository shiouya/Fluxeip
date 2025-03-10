package com.example.fluxeip.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

//@Entity
//@Table(name = "resources")
public class Resources {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer resourcesId;

	private String ResourcesName;

	@OneToMany(mappedBy = "Ressources")
	private List<Permissions> Ressources = new LinkedList<Permissions>();

	public Resources() {
	}

	public Integer getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(Integer resourcesId) {
		this.resourcesId = resourcesId;
	}

	public String getResourcesName() {
		return ResourcesName;
	}

	public void setResourcesName(String resourcesName) {
		ResourcesName = resourcesName;
	}

	public List<Permissions> getRessources() {
		return Ressources;
	}

	public void setRessources(List<Permissions> ressources) {
		Ressources = ressources;
	}

}
