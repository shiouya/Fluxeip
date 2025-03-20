package com.example.fluxeip.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskassignRequest {
	
	private String taskName;
	private String taskContent;
	private Date createDate;
	private Date expectedFinishDate;
	private Date finishDate;
	private String employee;
	private String status;
	

}
