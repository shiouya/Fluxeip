package com.example.fluxeip.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fluxeip.jwt.JsonWebTokenUtility;
import com.example.fluxeip.service.ClockService;


@Controller
@RequestMapping("/api/clock")
public class ClockController {

    @Autowired
    private ClockService clockService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @PostMapping("/in")
    public ResponseEntity<?> clockIn(@RequestHeader("Authorization") String authorization) {
        int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return ResponseEntity.badRequest().body("無效的token");
        }

        String message = clockService.clockIn(employeeId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/out")
    public ResponseEntity<String> clockOut(@RequestHeader("Authorization") String authorization) {
        int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return ResponseEntity.badRequest().body("無效的token");
        }

        String message = clockService.clockOut(employeeId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/field-work-start")
    public ResponseEntity<String> startFieldWork(@RequestHeader("Authorization") String authorization) {
        int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return ResponseEntity.badRequest().body("無效的token");
        }

        String message = clockService.startFieldWork(employeeId);
        return ResponseEntity.ok(message);
    } 

    @PostMapping("/field-work-end")
    public ResponseEntity<String> endFieldWork(@RequestHeader("Authorization") String authorization) {
        int employeeId = extractEmployeeIdFromToken(authorization);
        if (employeeId == -1) {
            return ResponseEntity.badRequest().body("無效的token");
        }

        String message = clockService.endFieldWork(employeeId);
        return ResponseEntity.ok(message);
    }

    private int extractEmployeeIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return -1; 
        }

        String token = authorization.substring(7);
        String userJsonString = jsonWebTokenUtility.validateToken(token); 

        if (userJsonString == null || userJsonString.isEmpty()) {
            return -1; 
        }

        try {
            JSONObject userJson = new JSONObject(userJsonString); 
            return userJson.getInt("id"); 
        } catch (Exception e) {
            e.printStackTrace(); 
            return -1;
        }
    }
}
