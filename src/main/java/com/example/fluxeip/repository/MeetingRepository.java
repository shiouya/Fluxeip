package com.example.fluxeip.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    boolean existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(Integer roomId, LocalDateTime endTime, LocalDateTime startTime);
    boolean existsByRoomIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(Integer roomId, LocalDateTime endTime, LocalDateTime startTime, Integer id);
    List<Meeting> findByRoomId(Integer roomId); // 根據 Room ID 查詢所有會議
 
    List<Meeting> findByEmployeeEmployeeId(Integer employeeId);

    List<Meeting> findAllByOrderByCreatedAtDesc();
    
    List<Meeting> findByRoomIdAndStartTimeBetween(Integer roomId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
   
}