package com.example.fluxeip.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "position")
@JsonIgnoreProperties({"employee"})  // 避免無限遞迴
public class Position {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "position_id")
	private Integer positionId;

	@Column(name = "position_name")
	private String positionName;

	@JsonIgnore
	@OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
	private List<Employee> employee = new LinkedList<Employee>();

	public Position() {
	}


}
