package com.example.fluxeip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalFlowDTO {
    private String flowName;
    private Integer requestTypeId;
    private Integer employeePositionId;
    private Integer stepOrder;
    private Integer approverPositionId;
}
