package com.example.fluxeip.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "guideline")
public class Guideline {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "guide_id")
	private int guideId;
	
	@Column(name = "guide_title")
	private String guideTitle; 
	
	@JsonIgnore
	@OneToMany(mappedBy = "guideline",cascade = CascadeType.ALL)
	private List<GuidelineContent> contents=new ArrayList<>();
	
	public Guideline() {
	}

	
	public Guideline(String guideTitle, String guideContent) {
		this.guideTitle = guideTitle;
	}


	public Guideline(int guideId, String guideTitle, String guideContent) {
		this.guideId = guideId;
		this.guideTitle = guideTitle;
	}


	public Guideline(int guideId, String guideTitle, String guideContent, List<GuidelineContent> contents) {
		this.guideId = guideId;
		this.guideTitle = guideTitle;
		this.contents = contents;
	}


	public int getGuideId() {
		return guideId;
	}


	public void setGuideId(int guideId) {
		this.guideId = guideId;
	}


	public String getGuideTitle() {
		return guideTitle;
	}


	public void setGuideTitle(String guideTitle) {
		this.guideTitle = guideTitle;
	}

}
