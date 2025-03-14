package com.example.fluxeip.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.ScheduleRequest;
import com.example.fluxeip.dto.ScheduleResponse;
import com.example.fluxeip.model.Schedule;
import com.example.fluxeip.service.ScheduleService;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

	@Autowired
	private ScheduleService scheduleService;

	@GetMapping("/week/{id}")
	public ResponseEntity<?> weeklySchedule(@PathVariable("id") Integer scheduleId,
			@RequestParam String startDate) {

		List<ScheduleResponse> response = scheduleService.findEmpScheduleWeek(scheduleId, startDate);
		if (response == null||response.size()==0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("查無班表");
		}
		return ResponseEntity.ok(response);
		
	}

	@GetMapping("/{id}")
	public ResponseEntity<ScheduleResponse> findScheduleById(@PathVariable("id") Integer scheduleId) {
		ScheduleResponse scheduleResponse = scheduleService.findScheduleResponseById(scheduleId);

		if (scheduleResponse == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(scheduleResponse);
	}

	@PostMapping
	public ResponseEntity<String> createSchedule(@RequestBody ScheduleRequest request) {
		try {
			scheduleService.createSchedule(request);

			return ResponseEntity.status(HttpStatus.CREATED).body("Schedule created successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating schedule: " + e.getMessage());
		}

	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateSchedule(@RequestBody ScheduleRequest request,
			@PathVariable("id") Integer scheduleId) {
		try {
			scheduleService.updateScheduleById(scheduleId, request);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating schedule: " + e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteSchedule(@PathVariable("id") Integer scheduleId) {

		boolean delete = scheduleService.deleteScheduleById(scheduleId);
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
