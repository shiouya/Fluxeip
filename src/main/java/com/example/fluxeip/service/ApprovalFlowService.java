//package com.example.fluxeip.service;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.example.fluxeip.model.ApprovalFlow;
//import com.example.fluxeip.model.Employee;
//import com.example.fluxeip.repository.ApprovalFlowRepository;
//import com.example.fluxeip.repository.EmployeeRepository;
//
//import jakarta.transaction.Transactional;
//
//@Transactional
//@Service
//public class ApprovalFlowService {
//
//    @Autowired
//    private ApprovalFlowRepository approvalFlowRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    // 根據請求類型和員工ID獲取簽核流程
//    public List<Employee> getApprovers(int employeeId, int requestTypeId) {
//        // 根據請求類型和員工ID查詢簽核流程
//        List<ApprovalFlow> approvalFlows = approvalFlowRepository.findByRequestTypeIdAndEmployeeId(requestTypeId, employeeId);
//
//        // 取得簽核流程中的職位ID
//        List<Integer> positionIds = new ArrayList<>();
//        for (ApprovalFlow flow : approvalFlows) {
//            positionIds.add(flow.getApproverPosition().getPositionId());
//        }
//
//        // 根據職位ID和員工部門查找可簽核的員工
//        return employeeRepository.findEmployeesByPositionAndDepartment(positionIds, employeeId);
//    } 
//} 
