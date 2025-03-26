package com.example.fluxeip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeApprovalFlowDTO {
    private Integer employeeId;   // 員工ID
    private Integer flowId;       // 簽核流程ID

}
