package com.example.fluxeip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.EmployeeApprovalFlowDTO;
import com.example.fluxeip.model.ApprovalFlow;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeApprovalFlow;
import com.example.fluxeip.model.Type;
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

    // 設定員工自訂簽核流程
    public void createEmployeeApprovalFlow(EmployeeApprovalFlowDTO dto) {
        // 驗證員工、申請類型、流程是否存在
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("員工不存在"));
        Type type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new RuntimeException("申請類型不存在"));
        ApprovalFlow approvalFlow = approvalFlowRepository.findById(dto.getFlowId())
                .orElseThrow(() -> new RuntimeException("簽核流程不存在"));

        // 創建並儲存自訂簽核流程
        EmployeeApprovalFlow employeeApprovalFlow = new EmployeeApprovalFlow();
        employeeApprovalFlow.setEmployee(employee);
        employeeApprovalFlow.setType(type);
        employeeApprovalFlow.setApprovalFlow(approvalFlow);

        employeeApprovalFlowRepository.save(employeeApprovalFlow);
    }
}
