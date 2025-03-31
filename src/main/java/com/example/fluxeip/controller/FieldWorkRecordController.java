package com.example.fluxeip.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.dto.FieldWorkRecordResponseDTO;
import com.example.fluxeip.model.FieldWorkRecord;
import com.example.fluxeip.service.FieldWorkRecordService;

@RestController
@RequestMapping("/api/field-work")
public class FieldWorkRecordController {

	@Autowired
	private FieldWorkRecordService fieldWorkRecordService;

	// 新增外勤紀錄
	@PostMapping("/create")
	public FieldWorkRecord createFieldWorkRecord(@RequestBody FieldWorkRecord record) {
		return fieldWorkRecordService.createFieldWorkRecord(record);
	}

	// 取得所有外勤紀錄
	@GetMapping("/all")
	public List<FieldWorkRecord> getAllFieldWorkRecords() {
		return fieldWorkRecordService.getAllFieldWorkRecords();
	}

	// 根據 ID 查詢外勤紀錄
	@GetMapping("/{id}")
	public ResponseEntity<FieldWorkRecordResponseDTO> getFieldWorkRecordById(@PathVariable Integer id) {
		return fieldWorkRecordService.getFieldWorkRecordById(id).map(ResponseEntity::ok) 
				.orElseGet(() -> ResponseEntity.notFound().build()); 
	}

	// 根據員工 ID 查詢外勤紀錄
	@GetMapping("/employee/{employeeId}")
	public ResponseEntity<?> getFieldWorkRecordsByEmployeeId(@PathVariable Integer employeeId) {
		return ResponseEntity.ok(fieldWorkRecordService.getFieldWorkRecordsByEmployeeId(employeeId));
	}

	// 根據日期範圍查詢外勤紀錄
	@GetMapping("/date-range")
	public List<FieldWorkRecord> getFieldWorkRecordsByDateRange(@RequestParam LocalDate startDate,
			@RequestParam LocalDate endDate) {
		return fieldWorkRecordService.getFieldWorkRecordsByDateRange(startDate, endDate);
	}

	// 更新外勤紀錄
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateFieldWorkRecord(@PathVariable Integer id,
			@RequestBody FieldWorkRecordResponseDTO updatedRecord) {
		return ResponseEntity.ok(fieldWorkRecordService.updateFieldWorkRecord(id, updatedRecord));
	}

	// 刪除外勤紀錄
	@DeleteMapping("/delete/{id}")
	public void deleteFieldWorkRecord(@PathVariable Integer id) {
		fieldWorkRecordService.deleteFieldWorkRecord(id);
	}
}
