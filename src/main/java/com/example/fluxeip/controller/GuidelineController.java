package com.example.fluxeip.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.GuidelineResponse;
import com.example.fluxeip.model.Guideline;
import com.example.fluxeip.model.GuidelineContent;
import com.example.fluxeip.service.GuidelineService;

@RestController
@CrossOrigin(origins = "*")
public class GuidelineController {

	@Autowired
	private GuidelineService guidelineService;
	
    @GetMapping("/api/guideline")
    public ResponseEntity<List<Guideline>> showGuileline(Model model) {
    	List<Guideline> allGuideline = guidelineService.findAllGuideline();
    	return ResponseEntity.ok(allGuideline); 
    }
    
	@GetMapping("/api/guideline/{id}")
	 public ResponseEntity<GuidelineResponse> getGuidelineWithContents(@PathVariable("id") Integer guideId) {
        Guideline guideline = guidelineService.findGuidelineById(guideId);
        if (guideline == null) {
            return ResponseEntity.notFound().build();
        }

        List<GuidelineContent> contents = guidelineService.findContentById(guideId);
        GuidelineResponse response = new GuidelineResponse(guideline, contents);
        return ResponseEntity.ok(response);
    }
	
}

