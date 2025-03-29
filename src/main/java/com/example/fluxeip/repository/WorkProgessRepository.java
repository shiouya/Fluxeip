package com.example.fluxeip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.WorkProgess;

public interface WorkProgessRepository extends JpaRepository<WorkProgess, Integer> {
	
	@Query("SELECT w FROM WorkProgess w WHERE w.workName LIKE %:name%")
    List<WorkProgess> findByName(String name);
	
	List<WorkProgess> findByStatus(Status status);
	
	@Query("SELECT w FROM WorkProgess w WHERE w.workName LIKE %:name% and w.status= :status")
    List<WorkProgess> findByNameAndStatus(String name,Status status);
	
	List<WorkProgess> findBySupervisorDepartment(Department department);
	
	List<WorkProgess> findBySupervisorDepartmentAndStatus(Department department,Status status);

	@Query("SELECT w FROM WorkProgess w WHERE w.workName LIKE %:name% and w.supervisor.department= :department")
    List<WorkProgess> findByNameAndDepartment(String name,Department department);
	
	@Query("SELECT w FROM WorkProgess w WHERE w.workName LIKE %:name% and w.supervisor.department= :department and w.status= :status")
    List<WorkProgess> findByNameAndDepartmentAndStatus(String name,Department department,Status status);
	
}
