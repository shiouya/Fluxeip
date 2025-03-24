package com.example.fluxeip.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkTaskCreateRequest {

	private Integer supervisorId;// 工作負責人id
	private String workName;
	private Date createDate;
	private Date expectedFinishdate;
	private List<TaskassignRequest> taskassigns;

}
