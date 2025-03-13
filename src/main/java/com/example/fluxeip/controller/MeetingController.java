package com.example.fluxeip.controller;

import com.example.fluxeip.dto.MeetingDTO;
import com.example.fluxeip.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;



    // 用id查會議
   @GetMapping("/{id}")
   public ResponseEntity<MeetingDTO> findById(@PathVariable Integer id){
	   Optional<MeetingDTO> optional = meetingService.findById(id);
	   
	   if(optional.isPresent()) {
		   return ResponseEntity.ok(optional.get());
	   }else {
		   return ResponseEntity.notFound().build();
	   }   
   }
   
   	// 用roomId查會議
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MeetingDTO>> findByRoomId(@PathVariable Integer roomId) {
        List<MeetingDTO> meetings = meetingService.findByRoomId(roomId);

        if (!meetings.isEmpty()) {
        	return ResponseEntity.ok(meetings); 
        } else {
        	return ResponseEntity.noContent().build(); 
        }
    }

    // 新增會議
    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody MeetingDTO meetingDTO) {
        Optional<MeetingDTO> savedMeeting = meetingService.create(meetingDTO);

        if (savedMeeting.isPresent()) {
            return ResponseEntity.ok(savedMeeting.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 更新會議
    @PutMapping("/{id}")
    public ResponseEntity<MeetingDTO> updateMeeting(@PathVariable Integer id, @RequestBody MeetingDTO meetingDTO) {
        Optional<MeetingDTO> updatedMeeting = meetingService.update(id, meetingDTO);

        if (updatedMeeting.isPresent()) {
            return ResponseEntity.ok(updatedMeeting.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // 刪除會議
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMeeting(@PathVariable Integer id) {
        boolean deleted = meetingService.delete(id);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "成功刪除會議 ID: " + id);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "找不到 ID 為 " + id + " 的會議");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
