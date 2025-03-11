package com.example.fluxeip.dto;

import java.util.List;

import com.example.fluxeip.model.Guideline;
import com.example.fluxeip.model.GuidelineContent;

public class GuidelineResponse {
	private Guideline guideline;
    private List<GuidelineContent> contents;

    public GuidelineResponse() {
	}

	public GuidelineResponse(Guideline guideline, List<GuidelineContent> contents) {
        this.guideline = guideline;
        this.contents = contents;
    }

    public Guideline getGuideline() {
        return guideline;
    }

    public void setGuideline(Guideline guideline) {
        this.guideline = guideline;
    }

    public List<GuidelineContent> getContents() {
        return contents;
    }

    public void setContents(List<GuidelineContent> contents) {
        this.contents = contents;
    }
}
