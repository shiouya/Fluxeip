package com.example.fluxeip.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateRequest {

	private Integer taskId;
	private String taskName;
	private String taskContent;
	private Date createDate;
	private Date expectedFinishDate;
	private Date finishDate;
	private String status;
	private String employee;// 交付的對象
	private Integer reveiew;// 審核對象

}
