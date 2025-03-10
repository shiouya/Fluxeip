package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
	List<Meeting> findByRoomId(Integer room);
}
