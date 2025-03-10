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
        List<Room> rooms = roomRepository.findAll();

        if (rooms.isEmpty()) {
            System.out.println("目前沒有任何會議室");
        } else {
            System.out.println("成功查詢所有會議室，共 " + rooms.size() + " 間");
        }

        return rooms;
    }


    
    public Optional<Room> findById(Integer id) {
        if (id == null) {
            System.out.println("錯誤：roomId 不能為 null");
            return Optional.empty();
        }

        Optional<Room> room = roomRepository.findById(id);

        if (room.isPresent()) {
            System.out.println("成功查詢到 ID 為 " + id + " 的會議室：" + room.get().getRoomName());
        } else {
            System.out.println("錯誤：找不到 ID 為 " + id + " 的會議室");
        }

        return room;
    }

    
    public Room insert(Room room) {
        if (room == null) {
            System.out.println("錯誤：room 不能為 null");
            return null;
        }

        if (room.getRoomName() == null || room.getRoomName().isEmpty()) {
            System.out.println("錯誤：roomName 不能為 null 或空");
            return null;
        }

        if (room.getLocation() == null || room.getLocation().isEmpty()) {
            System.out.println("錯誤：location 不能為 null 或空");
            return null;
        }

        System.out.println("正在插入會議室：" + room);
        return roomRepository.save(room);
    }


    
    public Optional<Room> update(Room room) {
        if (room == null || room.getId() == null) {
            System.out.println("錯誤：更新時 roomId 不能為 null");
            return Optional.empty();
        }

        Optional<Room> optional = roomRepository.findById(room.getId());

        if (optional.isPresent()) {
            Room insert = optional.get(); 
            insert.setRoomName(room.getRoomName());
            insert.setCapacity(room.getCapacity());
            insert.setImage(room.getImage());
            insert.setLocation(room.getLocation());

            Room updatedRoom = roomRepository.save(insert); 
            return Optional.of(updatedRoom);
        } else {
            System.out.println("錯誤：找不到 ID 為 " + room.getId() + " 的會議室");
            return Optional.empty();
        }
    }

    
    public boolean delete(Integer id) {
        if (id == null) {
            System.out.println("錯誤：roomId 不能為 null");
            return false;
        }

        Optional<Room> optional = roomRepository.findById(id);
        
        if (optional.isPresent()) {
            roomRepository.delete(optional.get()); 
            System.out.println("成功刪除 ID 為 " + id + " 的會議室");
            return true;
        } else {
            System.out.println("錯誤：找不到 ID 為 " + id + " 的會議室");
            return false;
        }
    }

    
    public boolean insertImage(Integer roomId, MultipartFile file) {
        if (roomId == null) {
            System.out.println("錯誤：roomId 不能為 null");
            return false;
        }

        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isEmpty()) {
            System.out.println("錯誤：找不到 ID 為 " + roomId + " 的會議室");
            return false;
        }

        Room room = optional.get();
        if (file == null || file.isEmpty()) { 
            System.out.println("沒有提供圖片，會議室 ID: " + roomId + " 的圖片未變更");
            return false;
        }

        try {
            byte[] bytes = file.getBytes();
            room.setImage(bytes);
            roomRepository.save(room);  

            System.out.println("成功上傳圖片到會議室 ID: " + roomId + "，圖片大小：" + bytes.length + " bytes");
            return true;
        } catch (IOException e) {
            System.out.println("錯誤：無法讀取圖片檔案 - " + e.getMessage());
            return false;
        }
    }


    
    public boolean exists(Integer id) {
        return id != null && roomRepository.existsById(id);
    }
}
