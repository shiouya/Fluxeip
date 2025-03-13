package com.example.fluxeip.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.ContactsDto;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeDetail;
import com.example.fluxeip.repository.EmployeeDetailRepository;
import com.example.fluxeip.repository.EmployeeRepository;

@Service
public class ContactService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeDetailRepository employeeDetailRepository;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeDetailService employeeDetailService;
	
	public List<ContactsDto> findAllEmpContact(){
		
		List<ContactsDto> contacts= new ArrayList<ContactsDto>();
		List<Employee> emps = employeeRepository.findAll();
		
		for(Employee emp:emps) {
			ContactsDto contact = new ContactsDto();
			EmployeeDetail empDet = employeeDetailService.empDetByIdFind(emp.getEmployeeId());
			
			contact.setDepartment(emp.getDepartment().getDepartmentName());
			contact.setEmail(empDet.getEmail());
			contact.setName(emp.getEmployeeName());
			contact.setPhone(empDet.getPhone());
			contact.setPhoto(empDet.getEmployeePhoto());
			contact.setPosition(emp.getPosition().getPositionName());
			contact.setEmpId(emp.getEmployeeId());
			
			contacts.add(contact);
		}
		
		return contacts;
	}
	
}
