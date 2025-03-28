package com.example.fluxeip.model;

import java.time.LocalDateTime;

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

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
@Entity
@Table(name="notify")
public class Notify {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id ;
	
	@Column(name="receive_employee_id")
	private Integer receiveEmployeeId;
	
	
	@Column(name="message")
	private String message;
	
	@Column(name="create_time")
	private LocalDateTime createTime = LocalDateTime.now();
	
	@Column(name="is_read")
	private Boolean isRead = false;
	
	@ManyToOne
	@JoinColumn(name="receive_employee_id" , insertable = false, updatable = false)
	private Employee receiveEmployee;
		 
	
	
	
	
	
}
