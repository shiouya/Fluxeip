package com.example.fluxeip.controller;

import com.example.fluxeip.dto.MeetingRequest;
import com.example.fluxeip.dto.MeetingResponse;
import com.example.fluxeip.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;
    
    
    // 查詢有會議
    @GetMapping
    public ResponseEntity<List<MeetingResponse>> findAll(){
    	List<MeetingResponse> meetings = meetingService.findAll();
    	
    	if(meetings.isEmpty()) {
    		return ResponseEntity.notFound().build();
    	}else {
    		return ResponseEntity.ok(meetings);
    	}
    }

    
    // 用Id查會議
    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> findById(@PathVariable Integer id){
    	Optional<MeetingResponse> optMeetings = meetingService.findById(id);
    	
    	if(optMeetings.isPresent()) {
    		return ResponseEntity.ok(optMeetings.get());
    	}else {
    		return ResponseEntity.notFound().build();
    	}
    	
    }

 // 用roomId查會議
    @GetMapping("/room/{roomId}")
    public ResponseEntity <List<MeetingResponse>> findByRoomId(@PathVariable Integer roomId){
    	List<MeetingResponse> meetings = meetingService.findByRoomId(roomId);
    	
    	if(!meetings.isEmpty()) {
    		return ResponseEntity.ok(meetings);
    	}else {
    		return ResponseEntity.notFound().build();
    	}
    }

    // 新增
    @PostMapping
    public ResponseEntity<MeetingResponse> creat(@RequestBody MeetingRequest meetingRequest){
    	
    	Optional<MeetingResponse> optmeeting = meetingService.create(meetingRequest);
    	
    	if(optmeeting.isPresent()) {
    		return ResponseEntity.status(201).body(optmeeting.get());
    	}else {
    		return ResponseEntity.badRequest().body(new MeetingResponse("會議室時段衝突或不符合規則"));
    	}
    			
    }
    
    // 更新
    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponse> update(@PathVariable Integer id,@RequestBody MeetingRequest meetingRequest){
    	
    	Optional<MeetingResponse> optmeeting = meetingService.update(id, meetingRequest);
    	
    	if(optmeeting.isPresent()) {
    		 return ResponseEntity.ok(optmeeting.get());
    	}else {
    		 return ResponseEntity.badRequest().body(new MeetingResponse("更新失敗，會議不存在或時段衝突"));
    	}
    	
    }
    
    
    // 刪除會議
    @DeleteMapping("/{id}")
    public ResponseEntity<MeetingResponse> deleteMeeting(@PathVariable Integer id) {
        
        boolean deleted = meetingService.delete(id);
      
        if (deleted) {
            return ResponseEntity.ok(new MeetingResponse("成功刪除會議"));
        } else {
            
            return ResponseEntity.badRequest().body(new MeetingResponse("刪除失敗，會議不存在"));
        }
    }


}
