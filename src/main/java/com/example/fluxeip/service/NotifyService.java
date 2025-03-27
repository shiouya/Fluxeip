package com.example.fluxeip.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.NotifyResponse;
import com.example.fluxeip.model.Notify;
import com.example.fluxeip.repository.NotifyRepository;

@Service
@Transactional
public class NotifyService {

	@Autowired
	private NotifyRepository notifyRepository;

	// 查詢某位員工的通知清單
	public List<NotifyResponse> findAllByEmployeeId(Integer employeeId) {

		List<Notify> notifies = notifyRepository.findByReceiveEmployeeIdOrderByCreateTimeDesc(employeeId);

		List<NotifyResponse> notifyResponse = new ArrayList<>();

		for (Notify notify : notifies) {
			notifyResponse.add(new NotifyResponse(notify));

		}
		return notifyResponse;
	}

	// 標記通知為已讀
	public Optional<NotifyResponse> markAsRead(Integer id) {

		if (id == null) {
			return Optional.empty();
		}

		Optional<Notify> optNotify = notifyRepository.findById(id);

		if (optNotify.isPresent()) {

			Notify notify = optNotify.get();

			notify.setIsRead(true);

			Notify savedNotify = notifyRepository.save(notify);

		    return Optional.of(new NotifyResponse(savedNotify));

		}
		return Optional.empty();
	}
	
	
	//發送通知
	public Optional<NotifyResponse> sendNotification(Integer receiveEmployeeId,  String message){
		
		if(receiveEmployeeId == null||message == null || message.isEmpty()) {
			return Optional.empty();
		}
		
		Notify notify = new Notify();
		
		notify.setReceiveEmployeeId(receiveEmployeeId);
		
		notify.setMessage(message);
		
		notify.setIsRead(false);
		
		
		Notify savedNotify = notifyRepository.save(notify);

	    return Optional.of(new NotifyResponse(savedNotify));
				
		
	}
		
	// 審核後的通知
	public Optional<NotifyResponse> sendMeetingApprovalResult(Integer receiveEmployeeId , String meetingTitle , boolean isApproved){
	    
	    if (receiveEmployeeId == null) {
	        return Optional.empty(); 
	    }
	    if (meetingTitle == null || meetingTitle.isEmpty()) {
	        return Optional.empty(); 
	    }

	    String resultText;
	    if (isApproved) {
	        resultText = "已通過審核";
	    } else {
	        resultText = "未通過審核";
	    }

	    String message = "您預約的會議《" + meetingTitle + "》" + resultText;

	    return sendNotification(receiveEmployeeId, message);
	}



}
