package com.example.fluxeip.controller;

import com.example.fluxeip.model.Room;
import com.example.fluxeip.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // 查詢所有會議室
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.findAll(); 
    }

    // 根據 ID 查詢會議室
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer id) {
        Optional<Room> optionalRoom = roomService.findById(id);
        if (optionalRoom.isPresent()) {
            return ResponseEntity.ok(optionalRoom.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 新增會議室
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomService.insert(room);
        if (savedRoom != null) {
            return ResponseEntity.ok(savedRoom);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 更新會議室
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Integer id, @RequestBody Room room) {
        room.setId(id); // 確保 ID 正確
        Optional<Room> updatedRoom = roomService.update(room);
        if (updatedRoom.isPresent()) {
            return ResponseEntity.ok(updatedRoom.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 刪除會議室
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer id) {
        boolean deleted = roomService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
