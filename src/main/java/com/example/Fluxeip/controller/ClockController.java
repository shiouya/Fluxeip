package com.example.fluxeip.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fluxeip.service.ClockService;


@Controller
@RequestMapping("/api/clock")
public class ClockController {

    @Autowired
    private ClockService clockService;

    @PostMapping("/in")
    public ResponseEntity<?> clockIn(@RequestParam int employeeId) {
    	System.out.println("上班"+employeeId);
        String message = clockService.clockIn(employeeId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/out")
    public ResponseEntity<String> clockOut(@RequestParam int employeeId) {
    	System.out.println("上班"+employeeId);
        String message = clockService.clockOut(employeeId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/field-work/start")
    public ResponseEntity<String> startFieldWork(@RequestParam int employeeId) {
    	System.out.println("外出打卡"+employeeId);
        String message = clockService.startFieldWork(employeeId);
        return ResponseEntity.ok(message);
    } 

    @PostMapping("/field-work/end")
    public ResponseEntity<String> endFieldWork(@RequestParam int employeeId) {
    	System.out.println("外出結束"+employeeId);
        String message = clockService.endFieldWork(employeeId);
        return ResponseEntity.ok(message);
    }
}
