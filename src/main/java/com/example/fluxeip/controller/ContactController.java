package com.example.fluxeip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.ContactsDto;
import com.example.fluxeip.service.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

	@Autowired
	private ContactService contactService;
	
	@GetMapping
	public List<ContactsDto> getAllContact(){
		return contactService.findAllEmpContact();
	}
}
