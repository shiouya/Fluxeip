package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.MissingPunchRequestDTO;
import com.example.fluxeip.dto.MissingPunchResponseDTO;
import com.example.fluxeip.dto.WorkAdjustmentRequestDTO;
import com.example.fluxeip.dto.WorkAdjustmentResponseDTO;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.LeaveRequest;
import com.example.fluxeip.model.MissingPunchRequest;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.repository.ApprovalStepRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.MissingPunchRequestRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;
import com.example.fluxeip.repository.WorkAdjustmentRequestRepository;

@Service
public class MissingPunchRequestService {

	@Autowired
	private MissingPunchRequestRepository missingPunchRequestRepository;
	    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TypeRepository typeRepository;
    
    @Autowired
    private StatusRepository statusRepository;
    
    @Autowired 
    private RequestIdGenerator requestIdGenerator;
    
    @Autowired
    private ApprovalFlowService approvalFlowService;  // 注入簽核流程 Service
    
    @Autowired
    private ApprovalStepRepository approvalStepRepository;
    
    @Transactional
    public List<MissingPunchRequest> getAllRequests() {
        return missingPunchRequestRepository.findAll();
    }
    
    @Transactional
    public Optional<MissingPunchRequest> getRequestById(Integer id) {
        return missingPunchRequestRepository.findById(id);
    }

    @Transactional
    public List<MissingPunchResponseDTO> getRequestsByEmployeeId(Integer employeeId) {
        List<MissingPunchRequest> requests = missingPunchRequestRepository.findByEmployee_EmployeeId(employeeId);

        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MissingPunchResponseDTO convertToDTO(MissingPunchRequest request) {
    	MissingPunchResponseDTO dto = new MissingPunchResponseDTO();
        dto.setMissingPunchRequestId(request.getId());
        dto.setEmployeeName(request.getEmployee().getEmployeeName()); // 假設 Employee 有 name 欄位
        dto.setClockType(request.getClockType().getTypeName()); // 假設 Type 有 typeName 欄位
        dto.setMissingDate(request.getMissingDate());
        dto.setReason(request.getReason());
        dto.setSubmittedAt(request.getSubmittedAt());
        dto.setStatus(request.getStatus().getStatusName()); // 假設 Status 有 id 欄位

        return dto;
    }

    @Transactional
    public String createRequest(MissingPunchRequestDTO requestDTO) {
    	MissingPunchRequest request = new MissingPunchRequest();
        
        // 設置 Employee 物件
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("員工不存在"));
        request.setEmployee(employee);

        // 設置 ClockType 物件
        Type clockType = typeRepository.findById(requestDTO.getClockTypeId())
                .orElseThrow(() -> new RuntimeException("打卡類型不存在"));
        request.setClockType(clockType );

        // 設置 Status 物件
        Status status = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new RuntimeException("狀態不存在"));
        request.setStatus(status);

        // 設置其他欄位
        request.setId(requestIdGenerator.getNextRequestId());
        request.setMissingDate(requestDTO.getMissingDate());
        request.setReason(requestDTO.getReason());
        request.setSubmittedAt(LocalDateTime.now());

        // 儲存請求
        Object missingPunchRequest = missingPunchRequestRepository.save(request);
        if (missingPunchRequest instanceof String) {
            return "申請失敗";
        }
        // 啟動補卡單的簽核流程
        try {
            approvalFlowService.startMissingPunchApprovalProcess(request);  // 呼叫簽核服務啟動流程
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return "啟動簽核流程時發生錯誤";
        }
        return "申請成功";
    }

    @Transactional
    public MissingPunchRequest updateRequest(Integer id, MissingPunchRequest updatedRequest) {
        return missingPunchRequestRepository.findById(id).map(request -> {
            request.setClockType(updatedRequest.getClockType());
            request.setMissingDate(updatedRequest.getMissingDate());
            request.setReason(updatedRequest.getReason());
            request.setStatus(updatedRequest.getStatus());
            return missingPunchRequestRepository.save(request);
        }).orElse(null);
    }

    public void deleteRequest(Integer id) {
    	
   	 // 取得與加減班單相關聯的所有審核步驟
       List<ApprovalStep> approvalStepByLeaveRequestId = approvalStepRepository.findApprovalStepByLeaveRequestId(id);

       // 逐一刪除每一個 ApprovalStep
       for (ApprovalStep approvalStep : approvalStepByLeaveRequestId) {
           approvalStepRepository.deleteById(approvalStep.getId()); // 刪除 ApprovalStep
       }
       
       // 刪除與補卡單單相關的 workAdjustmentRequest
       missingPunchRequestRepository.deleteById(id);
   }
}
