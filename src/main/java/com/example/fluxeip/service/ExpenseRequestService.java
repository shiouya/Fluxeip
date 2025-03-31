package com.example.fluxeip.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.ExpenseRequestDTO;
import com.example.fluxeip.dto.ExpenseResponseDTO;
import com.example.fluxeip.dto.MissingPunchRequestDTO;
import com.example.fluxeip.model.ApprovalStep;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.ExpenseRequest;
import com.example.fluxeip.model.MissingPunchRequest;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.model.Type;
import com.example.fluxeip.repository.ApprovalStepRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.ExpenseRequestRepository;
import com.example.fluxeip.repository.StatusRepository;
import com.example.fluxeip.repository.TypeRepository;

@Service
public class ExpenseRequestService {

	@Autowired
	private ExpenseRequestRepository expenseRequestRepository;
	    
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
    private FileService fileService;
    
    @Autowired
    private ApprovalStepRepository approvalStepRepository;
    
    @Transactional
    public List<ExpenseRequest> getAllRequests() {
        return expenseRequestRepository.findAll();
    }
    
    @Transactional
    public Optional<ExpenseRequest> getRequestById(Integer id) {
        return expenseRequestRepository.findById(id);
    }

    @Transactional
    public List<ExpenseResponseDTO> getRequestsByEmployeeId(Integer employeeId) {
        List<ExpenseRequest> requests = expenseRequestRepository.findByEmployee_EmployeeId(employeeId);

        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ExpenseResponseDTO convertToDTO(ExpenseRequest request) {
    	ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setExpenseRequestId(request.getId());
        dto.setEmployeeName(request.getEmployee().getEmployeeName()); // 假設 Employee 有 name 欄位
        dto.setExpenseType(request.getExpenseType().getTypeName()); // 假設 Type 有 typeName 欄位
        dto.setAmount(request.getAmount());
        dto.setDescription(request.getDescription());
        dto.setSubmittedAt(request.getSubmittedAt());
        String attachmentPath = request.getAttachments();
        dto.setAttachmentPath(attachmentPath);
        dto.setAttachmentName(fileService.extractOriginalFileName(attachmentPath));
        dto.setStatus(request.getStatus().getStatusName()); // 假設 Status 有 id 欄位

        return dto;
    }

    @Transactional
    public String createRequest(ExpenseRequestDTO requestDTO) {
    	ExpenseRequest request = new ExpenseRequest();
        
        // 設置 Employee 物件
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("員工不存在"));
        request.setEmployee(employee);

        // 設置 expenseType 物件
        Type expenseType = typeRepository.findById(requestDTO.getExpenseTypeId())
                .orElseThrow(() -> new RuntimeException("費用類型不存在"));
        request.setExpenseType(expenseType);

        // 設置 Status 物件
        Status status = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new RuntimeException("狀態不存在"));
        request.setStatus(status);

        // 設置其他欄位
        request.setId(requestIdGenerator.getNextRequestId());
        request.setAmount(requestDTO.getAmount());
        request.setDescription(requestDTO.getDescription());
        request.setAttachments(requestDTO.getAttachments());
        request.setSubmittedAt(LocalDateTime.now());

        // 儲存請求
        Object missingPunchRequest = expenseRequestRepository.save(request);
        if (missingPunchRequest instanceof String) {
            return "申請失敗";
        }
        // 啟動補卡單的簽核流程
        try {
            approvalFlowService.startExpenseApprovalProcess(request);  // 呼叫簽核服務啟動流程
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return "啟動簽核流程時發生錯誤";
        }
        return "申請成功";
    }

    public void deleteRequest(Integer id) {
    	
      	 // 取得與費用單相關聯的所有審核步驟
          List<ApprovalStep> approvalStepByLeaveRequestId = approvalStepRepository.findApprovalStepByLeaveRequestId(id);

          // 逐一刪除每一個 ApprovalStep
          for (ApprovalStep approvalStep : approvalStepByLeaveRequestId) {
              approvalStepRepository.deleteById(approvalStep.getId()); // 刪除 ApprovalStep
          }
          
          // 刪除與費用單相關的 workAdjustmentRequest
          expenseRequestRepository.deleteById(id);
      }
}
