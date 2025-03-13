package com.example.fluxeip.service;

import com.example.fluxeip.model.Room;
import com.example.fluxeip.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Optional<Room> findById(Integer id) {
        return roomRepository.findById(id);
    }

    public Room insert(Room room) {
        return roomRepository.save(room);
    }

    public Optional<Room> update(Room room) {
        if (room == null || room.getId() == null) {
            System.out.println("錯誤：更新失敗，room 或 id 為 null");
            return Optional.empty();
        }

        Optional<Room> optional = roomRepository.findById(room.getId());
        if (optional.isPresent()) {
            Room existingRoom = optional.get();
            existingRoom.setRoomName(room.getRoomName());
            existingRoom.setCapacity(room.getCapacity());
            existingRoom.setLocation(room.getLocation());

            Room updatedRoom = roomRepository.save(existingRoom);
            return Optional.of(updatedRoom);
        } else {
            System.out.println("錯誤：找不到 ID 為 " + room.getId() + " 的會議室");
            return Optional.empty();
        }
    }



    public boolean delete(Integer id) {
        if (id == null || !roomRepository.existsById(id)) {
            return false;
        }
        roomRepository.deleteById(id);
        return true;
    }

    public boolean insertImage(Integer roomId, MultipartFile file) {
        if (roomId == null || file == null || file.isEmpty()) {
            return false;
        }

        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isPresent()) {
            Room room = optional.get();
            try {
                room.setImage(file.getBytes());
                roomRepository.save(room);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
