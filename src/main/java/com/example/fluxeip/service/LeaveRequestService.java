package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.LeaveRequestRequest;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.LeaveRequestRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TypeRepository typeRepository;
    
    @Autowired
    private StatusRepository statusRepository;

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public Optional<LeaveRequest> getLeaveRequestById(Integer id) {
        return leaveRequestRepository.findById(id);
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeId(Integer employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public void deleteLeaveRequest(Integer id) {
        leaveRequestRepository.deleteById(id);
    }
    

    public Object createLeaveRequest(LeaveRequestRequest dto) {
        // 檢查員工是否存在
        Optional<Employee> employeeOpt = employeeRepository.findById(dto.getEmployeeId());
        if (!employeeOpt.isPresent()) {
            return "員工不存在";  // 直接返回錯誤訊息
        }
        Employee employee = employeeOpt.get();

        // 檢查請假類型是否存在
        Optional<Type> leaveTypeOpt = typeRepository.findById(dto.getLeaveTypeId());
        if (!leaveTypeOpt.isPresent()) {
            return "請假類型不存在";  // 直接返回錯誤訊息
        }
        Type leaveType = leaveTypeOpt.get();

        // 檢查狀態是否存在
        Optional<Status> statusOpt = statusRepository.findById(dto.getStatusId());
        if (!statusOpt.isPresent()) {
            return "狀態不存在";  // 直接返回錯誤訊息
        }
        Status status = statusOpt.get();
    	

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDatetime(dto.getStartDatetime());
        leaveRequest.setEndDatetime(dto.getEndDatetime());
        leaveRequest.setLeaveHours(dto.getLeaveHours());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(status);
        leaveRequest.setSubmittedAt(LocalDateTime.now());
        leaveRequest.setAttachments(dto.getAttachments()); // 附件路徑

        return leaveRequestRepository.save(leaveRequest); 
    }



}
