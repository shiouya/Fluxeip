package com.example.fluxeip.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "personal_calendar")
public class PersonalCalendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "employee_id", nullable = false)
	private Integer employeeId;
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
	
	@Column(name = "finish_date")
    private LocalDateTime finishDate;
    
	@Column(name = "title", nullable = false)
    private String content;
}
