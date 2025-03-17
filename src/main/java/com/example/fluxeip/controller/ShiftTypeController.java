package com.example.fluxeip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.ShiftTypeRequest;
import com.example.fluxeip.dto.ShiftTypeResponse;
import com.example.fluxeip.model.ShiftType;
import com.example.fluxeip.service.ShiftTypeService;

@RestController
@RequestMapping("/api/shiftType")
@CrossOrigin(origins = "*")
public class ShiftTypeController {

	@Autowired
	private ShiftTypeService shiftTypeService;
	
	@GetMapping
	public ResponseEntity<List<ShiftTypeResponse>> showAllShiftType(){
		
		List<ShiftTypeResponse> allShiftType = shiftTypeService.findAllShiftType();
		
		return ResponseEntity.ok(allShiftType);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ShiftTypeResponse> showShiftTypeById(@PathVariable("id") Integer shiftTypeId){
		
		ShiftTypeResponse shiftTypeResponse = shiftTypeService.findShiftTypeByIdToResponse(shiftTypeId);
		
		if(shiftTypeResponse==null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(shiftTypeResponse);
	}
	
	@PostMapping
	public ResponseEntity<String> createShiftType(@RequestBody ShiftTypeRequest shiftTypeRequest){
	    try {
	        shiftTypeService.createShiftType(shiftTypeRequest);
	        return ResponseEntity.status(HttpStatus.CREATED).body("Shift type created successfully");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	        		.body("Error creating shift type: " + e.getMessage());
	    }
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateShiftType(@RequestBody ShiftTypeRequest shiftTypeRequest,
			@PathVariable("id") Integer shiftTypeId){
		try {
	        shiftTypeService.updateShiftTypeById(shiftTypeId,shiftTypeRequest);
	        
	        return ResponseEntity.status(HttpStatus.OK).body("Shift type updated successfully");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	        		.body("Error updating shift type: " + e.getMessage());
	    }
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteShiftType(@PathVariable("id") Integer shiftTypeId){
		
		boolean delete = shiftTypeService.deleteShiftTypeById(shiftTypeId);
		
		Map<String, String> response = new HashMap<>();
		if (delete) {
			response.put("message", "success");
			response.put("success", "true");

			return ResponseEntity.ok(response); // 200 OK，帶回訊息
		} else {
			response.put("message", "false");
			response.put("success", "false");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 404 Not Found
		}
	}
}
