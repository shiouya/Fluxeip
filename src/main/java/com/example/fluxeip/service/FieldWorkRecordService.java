package com.example.fluxeip.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fluxeip.model.FieldWorkRecord;
import com.example.fluxeip.repository.FieldWorkRecordRepository;

@Service
public class FieldWorkRecordService {

    @Autowired
    private FieldWorkRecordRepository fieldWorkRecordRepository;

    // 新增外勤紀錄
    public FieldWorkRecord createFieldWorkRecord(FieldWorkRecord record) {
        return fieldWorkRecordRepository.save(record);
    }

    // 查詢所有外勤紀錄
    public List<FieldWorkRecord> getAllFieldWorkRecords() {
        return fieldWorkRecordRepository.findAll();
    }

    // 根據 ID 查詢外勤紀錄
    public Optional<FieldWorkRecord> getFieldWorkRecordById(Integer id) {
        return fieldWorkRecordRepository.findById(id);
    }

    // 根據員工 ID 查詢外勤紀錄
    public List<FieldWorkRecord> getFieldWorkRecordsByEmployeeId(Integer employeeId) {
        return fieldWorkRecordRepository.findByEmployee_EmployeeId(employeeId);
    }

    // 根據日期範圍查詢外勤紀錄
    public List<FieldWorkRecord> getFieldWorkRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return fieldWorkRecordRepository.findByFieldWorkDateBetween(startDate, endDate);
    }

    // 更新外勤紀錄
    public FieldWorkRecord updateFieldWorkRecord(Integer id, FieldWorkRecord updatedRecord) {
        return fieldWorkRecordRepository.findById(id).map(record -> {
            record.setFieldWorkDate(updatedRecord.getFieldWorkDate());
            record.setTotalHours(updatedRecord.getTotalHours());
            record.setLocation(updatedRecord.getLocation());
            record.setPurpose(updatedRecord.getPurpose());
            record.setStatus(updatedRecord.getStatus());
            return fieldWorkRecordRepository.save(record);
        }).orElse(null);
    }

    // 刪除外勤紀錄
    public void deleteFieldWorkRecord(Integer id) {
        fieldWorkRecordRepository.deleteById(id);
    }
}