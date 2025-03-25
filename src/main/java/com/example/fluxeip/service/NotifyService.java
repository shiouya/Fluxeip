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

		if (notifies.isEmpty()) {
			return new ArrayList<>();
		}

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
	public Optional<NotifyResponse> sendNotification(Integer receiveEmployeeId, Integer approvalStepId, String message){
		
		if(receiveEmployeeId == null|| approvalStepId == null ||message == null || message.isEmpty()) {
			return Optional.empty();
		}
		
		Notify notify = new Notify();
		
		notify.setReceiveEmployeeId(receiveEmployeeId);
		
		notify.setApprovalStepId(approvalStepId);
		
		notify.setMessage(message);
		
		notify.setIsRead(false);
		
		
		Notify savedNotify = notifyRepository.save(notify);

	    return Optional.of(new NotifyResponse(savedNotify));
				
		
	}
		
		
		


}
