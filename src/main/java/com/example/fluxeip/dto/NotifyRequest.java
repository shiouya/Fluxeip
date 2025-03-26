package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyRequest {
	
	
	private Integer receiveEmployeeId;
	
	private Integer approvalStepId;
	
	private String message;
	

}
