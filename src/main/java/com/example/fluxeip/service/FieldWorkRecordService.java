package com.example.fluxeip.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.dto.FieldWorkRecordResponseDTO;
import com.example.fluxeip.dto.WorkAdjustmentResponseDTO;
import com.example.fluxeip.model.FieldWorkRecord;
import com.example.fluxeip.model.WorkAdjustmentRequest;
import com.example.fluxeip.repository.FieldWorkRecordRepository;
import com.example.fluxeip.repository.StatusRepository;

@Service
public class FieldWorkRecordService {

    @Autowired
    private FieldWorkRecordRepository fieldWorkRecordRepository;
    
    @Autowired
    private StatusRepository statusRepository;

    // 新增外勤紀錄
    public FieldWorkRecord createFieldWorkRecord(FieldWorkRecord record) {
        return fieldWorkRecordRepository.save(record);
    }

    // 查詢所有外勤紀錄
    public List<FieldWorkRecord> getAllFieldWorkRecords() {
        return fieldWorkRecordRepository.findAll();
    }

    // 根據 ID 查詢外勤紀錄
    public Optional<FieldWorkRecordResponseDTO> getFieldWorkRecordById(Integer id) {
        return fieldWorkRecordRepository.findById(id)
                .map(this::convertToDTO); 
    }


    // 根據員工 ID 查詢外勤紀錄
    public List<FieldWorkRecordResponseDTO> getFieldWorkRecordsByEmployeeId(Integer employeeId) {
    	List<FieldWorkRecord> records = fieldWorkRecordRepository.findByEmployee_EmployeeId(employeeId);
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private FieldWorkRecordResponseDTO convertToDTO(FieldWorkRecord record) {
    	FieldWorkRecordResponseDTO dto = new FieldWorkRecordResponseDTO();
    	dto.setRecordId(record.getId());
        dto.setEmployeeId(record.getEmployee().getEmployeeId());
        dto.setEmployeeName(record.getEmployee().getEmployeeName());
        dto.setFieldWorkDate(record.getFieldWorkDate()); 
        dto.setLocation(record.getLocation()); 
        dto.setPurpose(record.getPurpose()); 
        dto.setTotalHours(record.getTotalHours()); 
        dto.setStatus(record.getStatus().getStatusName());
        return dto;
    }

    
    
    
    // 根據日期範圍查詢外勤紀錄
    public List<FieldWorkRecord> getFieldWorkRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return fieldWorkRecordRepository.findByFieldWorkDateBetween(startDate, endDate);
    }

 // 更新外勤紀錄並回傳 DTO
    public FieldWorkRecordResponseDTO updateFieldWorkRecord(Integer id, FieldWorkRecordResponseDTO updatedRecord) {
        // 根據ID查找外勤紀錄
        return fieldWorkRecordRepository.findById(id).map(record -> {
            // 更新紀錄的字段
            record.setFieldWorkDate(updatedRecord.getFieldWorkDate());
            record.setTotalHours(updatedRecord.getTotalHours());
            record.setLocation(updatedRecord.getLocation());
            record.setPurpose(updatedRecord.getPurpose());
            record.setStatus(statusRepository.findByStatusNameAndStatusType(updatedRecord.getStatus(),"外勤表單狀態").get() ); 

            // 儲存更新後的紀錄
            FieldWorkRecord updated = fieldWorkRecordRepository.save(record);

            // 將更新後的紀錄轉換成 DTO 並回傳
            return new FieldWorkRecordResponseDTO(id, updatedRecord.getEmployeeId(),
            	updatedRecord.getEmployeeName(),
                updated.getFieldWorkDate(),
                updated.getTotalHours(),
                updated.getLocation(),
                updated.getPurpose(),
                updated.getStatus().getStatusName()
            );
        }).orElse(null); // 如果找不到該 ID 的紀錄，則返回 null
    }

    // 刪除外勤紀錄
    public void deleteFieldWorkRecord(Integer id) {
        fieldWorkRecordRepository.deleteById(id);
    }
}