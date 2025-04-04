package com.example.fluxeip.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="attendee")
public class Attendee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="employee_id", nullable = false)
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="meeting_id", nullable = false)
	private Meeting meeting;
	
	@Column(name="is_attending")
	private Boolean isAttending;
	
	@Column(name="respond_time")
	private LocalDateTime respondTime;
	
	
	
	
	
	

}
