package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Attendee;

public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
	
	List<Attendee> findByEmployeeEmployeeId (Integer emoployeeId);
	
	List<Attendee> findByMeetingId (Integer meetingId);

}
