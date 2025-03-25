package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Notify;

public interface NotifyRepository extends JpaRepository<Notify, Integer> {

    List<Notify> findByReceiveEmployeeIdOrderByCreateTimeDesc(Integer employeeId);
}