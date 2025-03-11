package com.example.fluxeip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "guideline_content")
public class GuidelineContent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "guideline_content_id")
	private Integer guidelineContentId;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "fk_guide_id")
	private Guideline guideline;
	
	@Column(name = "content_type")
	private String contentType;
	
	@Column(name = "text_content")
	private String textContent;
	
	@Column(name = "image_content")
	private String imageContent;
	
	public GuidelineContent() {
	}

	
	
	public GuidelineContent(Integer guidelineContentId, Guideline guideline, String contentType, String textContent) {
		this.guidelineContentId = guidelineContentId;
		this.guideline = guideline;
		this.contentType = contentType;
		this.textContent = textContent;
	}



	public GuidelineContent(Integer guidelineContentId, Guideline guideline, String contentType, String textContent,
			String imageContent) {
		this.guidelineContentId = guidelineContentId;
		this.guideline = guideline;
		this.contentType = contentType;
		this.textContent = textContent;
		this.imageContent = imageContent;
	}



	public Integer getGuidelineContentId() {
		return guidelineContentId;
	}



	public void setGuidelineContentId(Integer guidelineContentId) {
		this.guidelineContentId = guidelineContentId;
	}



	public Guideline getGuideline() {
		return guideline;
	}



	public void setGuideline(Guideline guideline) {
		this.guideline = guideline;
	}



	public String getContentType() {
		return contentType;
	}



	public void setContentType(String contentType) {
		this.contentType = contentType;
	}



	public String getTextContent() {
		return textContent;
	}



	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}



	public String getImageContent() {
		return imageContent;
	}



	public void setImageContent(String imageContent) {
		this.imageContent = imageContent;
	}

}
