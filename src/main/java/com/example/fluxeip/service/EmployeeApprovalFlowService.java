package com.example.fluxeip.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.EmployeeApprovalFlowDTO;
import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeApprovalFlow;
import com.example.fluxeip.repository.ApprovalFlowRepository;
import com.example.fluxeip.repository.EmployeeApprovalFlowRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.TypeRepository;

@Service
public class EmployeeApprovalFlowService {
	@Autowired
    private EmployeeApprovalFlowRepository employeeApprovalFlowRepository;

    @Autowired
    private ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void createEmployeeApprovalFlows(List<EmployeeApprovalFlowDTO> dtos) {
        List<EmployeeApprovalFlow> approvalFlows = new ArrayList<>();

        for (EmployeeApprovalFlowDTO dto : dtos) {
            // 驗證員工、簽核流程是否存在
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("員工不存在: " + dto.getEmployeeId()));

            ApprovalFlow approvalFlow = approvalFlowRepository.findById(dto.getFlowId())
                    .orElseThrow(() -> new RuntimeException("簽核流程不存在: " + dto.getFlowId()));

            // 創建自訂簽核流程
            EmployeeApprovalFlow employeeApprovalFlow = new EmployeeApprovalFlow();
            employeeApprovalFlow.setEmployee(employee);
            employeeApprovalFlow.setType(approvalFlow.getRequestType());
            employeeApprovalFlow.setApprovalFlow(approvalFlow);

            approvalFlows.add(employeeApprovalFlow);
        }

        // 批量儲存
        employeeApprovalFlowRepository.saveAll(approvalFlows);
    }

}
