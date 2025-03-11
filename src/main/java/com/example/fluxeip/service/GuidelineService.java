package com.example.fluxeip.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.model.Guideline;
import com.example.fluxeip.model.GuidelineContent;
import com.example.fluxeip.repository.GuidelineContentRepository;
import com.example.fluxeip.repository.GuidelineRepository;


@Service
@Transactional
public class GuidelineService {

	@Autowired
	private GuidelineRepository guidelineRepository;
	@Autowired
	private GuidelineContentRepository contentRepository;

	public List<Guideline> findAllGuideline(){
		List<Guideline> guidelines = guidelineRepository.findAll();
		
		return guidelines;
	}
	
	public Guideline findGuidelineById(Integer id) {
		Optional<Guideline> guideline = guidelineRepository.findById(id);
		
		if(guideline!=null) {
			return guideline.get();
		}else {
			return null;
		}
	}
	
	public List<GuidelineContent> findContentById(Integer id){
		return contentRepository.findByGuidelineGuideId(id);
	}
	
	public void createGuideline(Guideline guideline) {
	    guidelineRepository.save(guideline);
	}
}
