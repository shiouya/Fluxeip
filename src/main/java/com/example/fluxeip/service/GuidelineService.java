package com.example.fluxeip.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.fluxeip.dto.GuidelineResponse;
import com.example.fluxeip.model.Guideline;
import com.example.fluxeip.model.GuidelineContent;
import com.example.fluxeip.repository.GuidelineContentRepository;
import com.example.fluxeip.repository.GuidelineRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class GuidelineService {

	@Autowired
	private GuidelineRepository guidelineRepository;
	@Autowired
	private GuidelineContentRepository contentRepository;

	public List<Guideline> findAllGuideline() {
		List<Guideline> guidelines = guidelineRepository.findAll();

		return guidelines;
	}

	public Guideline findGuidelineById(Integer id) {
		Optional<Guideline> guideline = guidelineRepository.findById(id);

		if (guideline != null) {
			return guideline.get();
		} else {
			return null;
		}
	}

	public List<GuidelineContent> findContentById(Integer id) {
		return contentRepository.findByGuidelineGuideId(id);
	}

	@Transactional
	public void createGuidelineWithContents(GuidelineResponse guidelineResponse, List<MultipartFile> files) throws Exception {

		Guideline guideline = guidelineResponse.getGuideline();
		// 先存 Guideline，讓它獲得 ID
		guidelineRepository.save(guideline);

		List<GuidelineContent> contents = guidelineResponse.getContents();

		uploadPhotosAndSetContent(contents, files, guideline.getGuideId());
		// 設置 Guideline 關聯並存入每個 Content
		for (GuidelineContent content : contents) {
			content.setGuideline(guideline);
			contentRepository.save(content);
		}
	}

	@Transactional
	public void updateGuidelineWithContents(Integer guidelineId,GuidelineResponse guidelineResponse, List<MultipartFile> files) throws Exception {
		

		// 先檢查 Guideline 是否存在
		Guideline existingGuideline = guidelineRepository.findById(guidelineId).orElse(null);
		if (existingGuideline == null) {
			throw new RuntimeException("Guideline 不存在，無法更新");
		}

		Guideline updatedGuideline = guidelineResponse.getGuideline();
		List<GuidelineContent> updatedContents = guidelineResponse.getContents();
		// **更新 Guideline 資料**
		existingGuideline.setGuideTitle(updatedGuideline.getGuideTitle());
		guidelineRepository.save(existingGuideline);

		deletePhotosByGuideId(guidelineId);
		// **刪除舊的 GuidelineContent**
		contentRepository.deleteByGuidelineGuideId(guidelineId);
		
		uploadPhotosAndSetContent(updatedContents, files, guidelineId);

		// **儲存新的 GuidelineContent**
		for (GuidelineContent content : updatedContents) {
			content.setGuideline(existingGuideline); // 綁定 Guideline
			contentRepository.save(content);
		}
	}

	@Transactional
	public boolean deleteGuidelineById(Integer guidelineId) {
		// 先檢查 Guideline 是否存在
		if (!guidelineRepository.existsById(guidelineId)) {
			throw new RuntimeException("Guideline 不存在，無法刪除");
		}
		
		deletePhotosByGuideId(guidelineId);
		// 刪除 Guideline（對應的 GuidelineContent 會自動刪除）
		guidelineRepository.deleteById(guidelineId);
		return true;
	}
	
	private void uploadPhotosAndSetContent(List<GuidelineContent> contents, List<MultipartFile> files, Integer guideId) throws Exception{
		int imageIndex = 0;
		String userDir = System.getProperty("user.dir");
		for (GuidelineContent content : contents) {
			if ("image".equals(content.getContentType()) && files != null && imageIndex < files.size()) {
				MultipartFile file = files.get(imageIndex++);
				String uploadDir = userDir + "/uploads/images/guideline/";
				String fileName = "智庫_"+guideId+"_圖片_" + imageIndex+"_"+file.getOriginalFilename();
				File destFile = new File(uploadDir + fileName);
				file.transferTo(destFile);

				// 設定圖片對應的路徑
				content.setImageContent("/uploads/images/guideline/" + fileName);
			}
		}
	}
	
	private void deletePhotosByGuideId(Integer guideId) {
		
		List<GuidelineContent> contents = findContentById(guideId);
		String userDir = System.getProperty("user.dir");
		for (GuidelineContent content : contents) {
			if ("image".equals(content.getContentType())) {
				File file = new File(userDir+content.getImageContent());
				Path path = Paths.get(userDir+content.getImageContent());
				if (file.exists()) {
		            // 刪除圖片
					try {
						Files.delete(path);
						System.out.println("刪除成功");
					} catch (IOException e) {
						System.out.println("刪除失敗");
					}
		            
		        } else {
		            System.out.println("圖片不存在: ");
		        }
			}
		}
	}
}
