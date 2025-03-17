package com.example.fluxeip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fluxeip.dto.GuidelineResponse;
import com.example.fluxeip.model.Guideline;
import com.example.fluxeip.model.GuidelineContent;
import com.example.fluxeip.service.GuidelineService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/guidelines")
@CrossOrigin(origins = "*")
public class GuidelineController {

	@Autowired
	private GuidelineService guidelineService;

	@GetMapping
	public ResponseEntity<List<Guideline>> showGuileline(Model model) {
		List<Guideline> allGuideline = guidelineService.findAllGuideline();
		return ResponseEntity.ok(allGuideline);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GuidelineResponse> getGuidelineWithContents(@PathVariable("id") Integer guideId) {
		Guideline guideline = guidelineService.findGuidelineById(guideId);
		if (guideline == null) {
			return ResponseEntity.notFound().build();
		}

		List<GuidelineContent> contents = guidelineService.findContentById(guideId);
		GuidelineResponse response = new GuidelineResponse(guideline, contents);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<String> createGuideline(@RequestParam("data") String jsonData,
			@RequestParam(value = "files", required = false) List<MultipartFile> files) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			GuidelineResponse guidelineResponse = objectMapper.readValue(jsonData, GuidelineResponse.class);
			guidelineService.createGuidelineWithContents(guidelineResponse, files);
			return ResponseEntity.status(HttpStatus.CREATED).body("Guideline created successfully!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateGuidelineWithContents(@PathVariable("id") Integer guidelineId,
			@RequestParam("data") String jsonData,
			@RequestParam(value = "files", required = false) List<MultipartFile> files) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			GuidelineResponse guidelineResponse = objectMapper.readValue(jsonData, GuidelineResponse.class);
			
			guidelineService.updateGuidelineWithContents(guidelineId, guidelineResponse, files);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing request: " + e.getMessage());
		}

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteGuideline(@PathVariable("id") Integer guidelineId) {
		boolean delete = guidelineService.deleteGuidelineById(guidelineId);
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
