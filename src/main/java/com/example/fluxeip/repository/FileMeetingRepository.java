package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.FileMeeting;


public interface FileMeetingRepository extends JpaRepository<FileMeeting, Integer> {
    List<FileMeeting> findByMeetingId(Integer meetingId);
}
