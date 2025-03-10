package com.example.fluxeip.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="room")
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="room_name",nullable = false, length = 100)
	private String roomName;
	
	@Column(name="capacity",nullable = false)
	private int capacity;
	
	@Lob
	@Column(name = "image", columnDefinition = "VARBINARY(MAX)",nullable = true)
	private byte[] image;
	
	@Column(name="location",nullable = false, length = 100)
	private String location;
	
	
	
}
