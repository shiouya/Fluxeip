package com.example.Fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Fluxeip.model.Status;
import com.example.Fluxeip.repository.StatusRepository;

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

}
