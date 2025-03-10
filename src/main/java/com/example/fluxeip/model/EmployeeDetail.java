package com.example.fluxeip.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee_detail")
public class EmployeeDetail {

	@Id
	@Column(name = "employee_id")
	private Integer employeeId;

	@OneToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	private String gender;

	private Date birthday;

	@Column(name = "identity_card")
	private String identityCard;

	private String email;

	private String phone;

	@Column(name = "employee_photo")
	private byte[] employeePhoto;

	private String address;

	@Column(name = "emergency_contact")
	private String emergencyContact;

	@Column(name = "emergency_phone")
	private String energencyPhone;

	public EmployeeDetail() {
	}

}
