package com.example.fluxeip.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.DepartmentCalendar;
import com.example.fluxeip.repository.DepartmentCalendarRepository;
import com.example.fluxeip.repository.DepartmentRepository;
import com.example.fluxeip.dto.DepartmentCalendarRequestDTO;

@Service
public class DepartmentCalendarService {

    private final DepartmentCalendarRepository departmentCalendarRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentCalendarService(DepartmentCalendarRepository departmentCalendarRepository, DepartmentRepository departmentRepository) {
        this.departmentCalendarRepository = departmentCalendarRepository;
        this.departmentRepository = departmentRepository;
    }

    // 根據部門 ID 查詢所有事件
    public List<DepartmentCalendar> getEventsByDepartment(Integer departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部門未找到"));
        return departmentCalendarRepository.findByDepartment_DepartmentId(departmentId);
    }

    // 創建事件
    public DepartmentCalendar createEvent(DepartmentCalendarRequestDTO requestDTO) {
        Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("部門未找到"));
        
        DepartmentCalendar newEvent = new DepartmentCalendar();
        newEvent.setDepartment(department);
        newEvent.setStartDate(requestDTO.getStartDate());
        newEvent.setFinishDate(requestDTO.getFinishDate());
        newEvent.setContent(requestDTO.getContent());

        // 驗證事件是否合法
        

        return departmentCalendarRepository.save(newEvent);
    }

    // 刪除事件
    public boolean deleteEvent(Integer id) {
        if (!departmentCalendarRepository.existsById(id)) {
            return false;
        }
        departmentCalendarRepository.deleteById(id);
        return true;
    }
}
