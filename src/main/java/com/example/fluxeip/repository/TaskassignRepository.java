package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;

public interface TaskassignRepository extends JpaRepository<Taskassign, Integer> {

	List<Taskassign> findByWorkprogess(WorkProgess workprogess);

}
