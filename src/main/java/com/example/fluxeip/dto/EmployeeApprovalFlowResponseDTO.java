package com.example.fluxeip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeApprovalFlowResponseDTO {
	private Integer id;
	private Integer flowId;
    private String flowName;
    private String requestType;
    private Integer stepOrder;
    private String employeePosition;
    private String approverPosition;
}
