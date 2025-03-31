package com.example.fluxeip.controller;

import com.example.fluxeip.dto.PersonalCalendarRequestDTO;
import com.example.fluxeip.dto.PersonalCalendarResponseDTO;
import com.example.fluxeip.model.PersonalCalendar;
import com.example.fluxeip.repository.PersonalCalendarRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/calendar/personal")
public class PersonalCalendarController {

    @Autowired
    private PersonalCalendarRepository personalCalendarRepository;

    // 獲取某一員工的所有事件
    @GetMapping("/{employeeId}")
    public List<PersonalCalendarResponseDTO> getAllEventsByEmployeeId(@PathVariable String employeeId) {
        return personalCalendarRepository.findByEmployeeId(employeeId)
                .stream()
                .map(event -> new PersonalCalendarResponseDTO(
                        event.getId(), event.getContent(), event.getStartDate(), event.getFinishDate(), event.getEmployeeId()))
                .collect(Collectors.toList());
    }

    // 新增事件
    @PostMapping
    public ResponseEntity<PersonalCalendarResponseDTO> createEvent(@RequestBody PersonalCalendarRequestDTO personalCalendarRequestDTO) {
        if (personalCalendarRequestDTO.getFinishDate() == null) {
            return ResponseEntity.badRequest().body(
                    new PersonalCalendarResponseDTO(null, "finishDate 不能為空", null, null, null)
            );
        }

        if (personalCalendarRequestDTO.getFinishDate().isBefore(personalCalendarRequestDTO.getStartDate())) {
            return ResponseEntity.badRequest().body(
                    new PersonalCalendarResponseDTO(null, "finishDate 不能早於 startDate", null, null, null)
            );
        }

        if (personalCalendarRequestDTO.getEmployeeId() == null || personalCalendarRequestDTO.getEmployeeId().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new PersonalCalendarResponseDTO(null, "employeeId 不能為空", null, null, null)
            );
        }

        PersonalCalendar personalCalendar = new PersonalCalendar();
        personalCalendar.setContent(personalCalendarRequestDTO.getContent());
        personalCalendar.setStartDate(personalCalendarRequestDTO.getStartDate());
        personalCalendar.setFinishDate(personalCalendarRequestDTO.getFinishDate());
        personalCalendar.setEmployeeId(personalCalendarRequestDTO.getEmployeeId()); // 設置創建者員工ID

        PersonalCalendar savedEvent = personalCalendarRepository.save(personalCalendar);

        PersonalCalendarResponseDTO responseDTO = new PersonalCalendarResponseDTO(
                savedEvent.getId(), savedEvent.getContent(), savedEvent.getStartDate(), savedEvent.getFinishDate(), savedEvent.getEmployeeId()
        );

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 刪除事件
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable int id) {
        // 查找事件
        PersonalCalendar event = personalCalendarRepository.findById(id).orElse(null);
        
        // 確保事件存在
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        // 刪除事件
        personalCalendarRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Event deleted successfully");
    }
}
