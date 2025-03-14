package com.example.fluxeip.controller;

import com.example.fluxeip.model.Room;
import com.example.fluxeip.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/rooms")
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
        Optional<Room> optional = roomService.findById(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
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
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }

        room.setId(id); // 確保 ID 設定正確
        Optional<Room> updatedRoom = roomService.update(room);
        return updatedRoom.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
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
    
    
    @PostMapping("/{roomId}/upload-image")
    public ResponseEntity<String> uploadRoomImage(@PathVariable Integer roomId, @RequestParam(value = "file", required = false) MultipartFile file) {
        boolean success = roomService.insertImage(roomId, file);

        if (success) {
            return ResponseEntity.ok("圖片成功上傳到會議室 ID: " + roomId);
        } else {
            return ResponseEntity.badRequest().body("沒有提供圖片或圖片上傳失敗");
        }
    }
    
    @GetMapping("/{roomId}/image")
    public ResponseEntity<byte[]> getRoomImage(@PathVariable Integer roomId) {
        Optional<Room> optional = roomService.findById(roomId);

        if (optional.isPresent()) {
            Room room = optional.get();
            if (room.getImage() != null) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                        .body(room.getImage());
            } else {
                System.out.println("會議室 ID: " + roomId + " 沒有圖片");
                return ResponseEntity.notFound().build(); 
            }
        } else {
            System.out.println(" 找不到會議室 ID: " + roomId);
            return ResponseEntity.notFound().build();
        }
    }
    
}
