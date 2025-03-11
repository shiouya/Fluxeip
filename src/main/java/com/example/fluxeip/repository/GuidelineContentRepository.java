package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.GuidelineContent;


public interface GuidelineContentRepository extends JpaRepository<GuidelineContent, Integer> {

	public List<GuidelineContent> findByGuidelineGuideId(Integer guideId);
	
    public void deleteByGuidelineGuideId(Integer guideId);

}
