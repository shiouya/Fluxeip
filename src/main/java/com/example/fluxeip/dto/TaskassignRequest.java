package com.example.fluxeip.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TaskassignRequest {
	
	private String taskName;
	private String taskContent;
	private Date createDate;
	private Date expectedFinishDate;
	private String employee;// 交付的對象
	private String reveiew;// 審核對象
	

}
