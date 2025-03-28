package com.example.fluxeip.controller;

import com.example.fluxeip.dto.NotifyRequest;
import com.example.fluxeip.dto.NotifyResponse;
import com.example.fluxeip.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notify")
public class NotifyController {

	@Autowired
	private NotifyService notifyService;

	// 查詢員工的通知清單
	@GetMapping("/{employeeId}")
	public ResponseEntity<List<NotifyResponse>> getNotificationsByEmployeeId(@PathVariable Integer employeeId) {
		List<NotifyResponse> responseList = notifyService.findAllByEmployeeId(employeeId);
		return ResponseEntity.ok(responseList);
	}

	// 通知已讀
	@PutMapping("/read/{id}")
	public ResponseEntity<?> markAsRead(@PathVariable Integer id) {

		Optional<NotifyResponse> result = notifyService.markAsRead(id);

		if (result.isPresent()) {
			return ResponseEntity.ok(result.get());
		} else {

			return ResponseEntity.notFound().build();
		}
	}

	// 發送通知
	@PostMapping("/send")
	public ResponseEntity<?> sendNotification(@RequestBody NotifyRequest request) {

		Integer receiverId = request.getReceiveEmployeeId();

		String message = request.getMessage();

		Optional<NotifyResponse> result = notifyService.sendNotification(receiverId, message);

		if (result.isPresent()) {
			return ResponseEntity.ok(result.get());
		} else {
			return ResponseEntity.badRequest().body("通知建立失敗");
		}
	}

}
