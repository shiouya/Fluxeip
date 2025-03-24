package com.example.fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.StatusRepository;

@Service
public class StatusService {

	@Autowired
	private StatusRepository staRep;

	public Status findById(Integer id) {
		Optional<Status> status = staRep.findById(id);
		if (status != null) {
			return status.get();
		} else {
			return null;
		}
	}
	
	public Status findByName(String name) {
		Optional<Status> status = staRep.findByStatusName(name);
		if (status != null) {
			return status.get();
		} else {
			return null;
		}
	}
	
	public Status findByStatusNameAndStatusType(String statusName,String statusType) {
		return staRep.findByStatusNameAndStatusType(statusName, statusType).get();
		
	}

} 
