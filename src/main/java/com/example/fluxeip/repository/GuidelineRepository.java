package com.example.fluxeip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fluxeip.model.Guideline;


@Repository
public interface GuidelineRepository extends JpaRepository<Guideline, Integer> {

}
