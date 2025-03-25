package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.WorkAdjustmentRequestDTO;
import com.example.fluxeip.dto.WorkAdjustmentResponseDTO;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;
import com.example.fluxeip.repository.WorkAdjustmentRequestRepository;

@Service
public class WorkAdjustmentRequestService {

    @Autowired
    private WorkAdjustmentRequestRepository workAdjustmentRequestRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TypeRepository typeRepository;
    
    @Autowired
    private StatusRepository statusRepository;

    public List<WorkAdjustmentRequest> getAllRequests() {
        return workAdjustmentRequestRepository.findAll();
    }

    public Optional<WorkAdjustmentRequest> getRequestById(Integer id) {
        return workAdjustmentRequestRepository.findById(id);
    }

    public List<WorkAdjustmentResponseDTO> getRequestsByEmployeeId(Integer employeeId) {
        List<WorkAdjustmentRequest> requests = workAdjustmentRequestRepository.findByEmployee_EmployeeId(employeeId);

        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private WorkAdjustmentResponseDTO convertToDTO(WorkAdjustmentRequest request) {
        WorkAdjustmentResponseDTO dto = new WorkAdjustmentResponseDTO();
        dto.setWorkAdjustmentRequestId(request.getId());
        dto.setEmployeeName(request.getEmployee().getEmployeeName()); // 假設 Employee 有 name 欄位
        dto.setAdjustmentType(request.getAdjustmentType().getTypeName()); // 假設 Type 有 typeName 欄位
        dto.setAdjustmentDate(request.getAdjustmentDate());
        dto.setHours(request.getHours());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus().getStatusName()); // 假設 Status 有 id 欄位

        return dto;
    }

    public WorkAdjustmentRequest createRequest(WorkAdjustmentRequestDTO requestDTO) {
        WorkAdjustmentRequest request = new WorkAdjustmentRequest();
        
        // 設置 Employee 物件
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("員工不存在"));
        request.setEmployee(employee);

        // 設置 AdjustmentType 物件
        Type adjustmentType = typeRepository.findById(requestDTO.getAdjustmentTypeId())
                .orElseThrow(() -> new RuntimeException("調整類型不存在"));
        request.setAdjustmentType(adjustmentType);

        // 設置 Status 物件
        Status status = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new RuntimeException("狀態不存在"));
        request.setStatus(status);

        // 設置其他欄位
        request.setAdjustmentDate(requestDTO.getAdjustmentDate());
        request.setHours(requestDTO.getHours());
        request.setReason(requestDTO.getReason());
        request.setSubmittedAt(LocalDateTime.now());

        // 儲存請求
        return workAdjustmentRequestRepository.save(request);
    }


    public WorkAdjustmentRequest updateRequest(Integer id, WorkAdjustmentRequest updatedRequest) {
        return workAdjustmentRequestRepository.findById(id).map(request -> {
            request.setAdjustmentType(updatedRequest.getAdjustmentType());
            request.setAdjustmentDate(updatedRequest.getAdjustmentDate());
            request.setHours(updatedRequest.getHours());
            request.setReason(updatedRequest.getReason());
            request.setStatus(updatedRequest.getStatus());
            return workAdjustmentRequestRepository.save(request);
        }).orElse(null);
    }

    public void deleteRequest(Integer id) {
        workAdjustmentRequestRepository.deleteById(id);
    }
}
