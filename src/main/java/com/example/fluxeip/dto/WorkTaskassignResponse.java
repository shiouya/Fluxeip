package com.example.fluxeip.dto;

import java.util.List;

import com.example.fluxeip.model.Taskassign;
import com.example.fluxeip.model.WorkProgess;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkTaskassignResponse {

	public WorkProgess workprogress;
	public List<Taskassign> taskassign;

}
