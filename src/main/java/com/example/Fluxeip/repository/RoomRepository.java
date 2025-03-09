package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

}
