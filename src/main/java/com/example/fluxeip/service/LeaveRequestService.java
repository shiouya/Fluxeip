package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.LeaveRequestRequest;
import com.example.fluxeip.dto.LeaveRequestResponseDTO;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.repository.ApprovalStepRepository;
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
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private ApprovalFlowService approvalFlowService;
    
    @Autowired 
    private RequestIdGenerator requestIdGenerator;
    
    @Autowired
    private ApprovalStepRepository approvalStepRepository;

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequestResponseDTO getLeaveRequestById(Integer id) {
        Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(id);
        LeaveRequest leaveRequest = leaveRequestOpt.get();
        LeaveRequestResponseDTO dto = convertToDTO(leaveRequest);
        return dto;
    }

    // 根據員工 ID 查詢所有請假申請
    public List<LeaveRequestResponseDTO> getLeaveRequestsByEmployeeId(Integer employeeId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeId(employeeId);
        List<LeaveRequestResponseDTO> dtoList = new ArrayList<>();
        for (LeaveRequest leaveRequest : leaveRequests) {
            dtoList.add(convertToDTO(leaveRequest));
        }
        return dtoList;
    }

    private LeaveRequestResponseDTO convertToDTO(LeaveRequest leaveRequest) {
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();
        dto.setLeaveRequestId(leaveRequest.getId());
        dto.setEmployeeName(leaveRequest.getEmployee().getEmployeeName());
        dto.setLeaveType(leaveRequest.getLeaveType().getTypeName());
        dto.setStartDatetime(leaveRequest.getStartDatetime());
        dto.setEndDatetime(leaveRequest.getEndDatetime());
        dto.setLeaveHours(leaveRequest.getLeaveHours());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus().getStatusName());
        dto.setSubmittedAt(leaveRequest.getSubmittedAt());
        // 取得檔案名稱並處理
        String attachmentPath = leaveRequest.getAttachments();
        String attachmentName = fileService.extractOriginalFileName(attachmentPath);
        dto.setAttachmentPath(attachmentPath);
        dto.setAttachmentName(attachmentName);
        return dto;
    }

    public void deleteLeaveRequest(Integer id) {
        // 取得與請假單相關聯的所有審核步驟
        List<ApprovalStep> approvalStepByLeaveRequestId = approvalStepRepository.findApprovalStepByLeaveRequestId(id);

        // 逐一刪除每一個 ApprovalStep
        for (ApprovalStep approvalStep : approvalStepByLeaveRequestId) {
            approvalStepRepository.deleteById(approvalStep.getId()); // 刪除 ApprovalStep
        }
        
        // 刪除與請假單相關的 LeaveRequest
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
    	
        // 創建請假申請
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(requestIdGenerator.getNextRequestId());
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDatetime(dto.getStartDatetime());
        leaveRequest.setEndDatetime(dto.getEndDatetime());
        leaveRequest.setLeaveHours(dto.getLeaveHours());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(status);
        leaveRequest.setSubmittedAt(LocalDateTime.now());
        leaveRequest.setAttachments(dto.getAttachments()); // 附件路徑
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        // **初始化第一個簽核步驟**
//        approvalFlowService.startApprovalProcess(savedLeaveRequest);
        return savedLeaveRequest; 
    }



}
