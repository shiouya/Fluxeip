package com.example.fluxeip.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.EmployeeDetail;
import com.example.fluxeip.repository.EmployeeDetailRepository;

@Service
public class EmployeeDetailService {

	@Autowired
	private EmployeeDetailRepository empDetRes;

	public EmployeeDetail empDetCreate(EmployeeDetail entity) {
		EmployeeDetail empDet = empDetRes.save(entity);
		return empDet;
	}

	public EmployeeDetail empDetByIdFind(Integer id) {
		Optional<EmployeeDetail> empDet = empDetRes.findById(id);
		if (empDet.isPresent()) {
			EmployeeDetail bean = empDet.get();
			return bean;
		}
		return null;
	}
	
	public boolean isEmailExist(String email) {
        // 如果資料庫中有相同的信箱，則返回 true
        return empDetRes.findByEmail(email).isPresent();
    }
	public boolean isIdentityCardExist(String identityCard) {
		// 如果資料庫中有相同的身分證，則返回 true
		return empDetRes.findByIdentityCard(identityCard).isPresent();
	}
	public boolean isPhoneExist(String phone) {
		// 如果資料庫中有相同的電話，則返回 true
		return empDetRes.findByPhone(phone).isPresent();
	}

}
