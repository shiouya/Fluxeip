package com.example.fluxeip.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkUpdateRequest {

	private Integer workId;
	private String workName;
	private Date createDate;
	private Date expectedFinishdate;
	private LocalDate finishdate;
	private String status;
	private List<TaskUpdateRequest> taskassigns;

}
