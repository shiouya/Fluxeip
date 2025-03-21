package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fluxeip.model.Roles;

public interface RolesRepository extends JpaRepository<Roles, Integer> {

	Roles findByRoleName(String roleName);

}
