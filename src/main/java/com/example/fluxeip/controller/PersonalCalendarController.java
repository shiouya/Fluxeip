package com.example.fluxeip.controller;  // 修改包名

import com.example.fluxeip.model.PersonalCalendar;  // 引用新的包
import com.example.fluxeip.repository.PersonalCalendarRepository;  // 引用新的包
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar/personal")
public class PersonalCalendarController {

    @Autowired
    private PersonalCalendarRepository personalCalendarRepository;

    // 獲取所有事件
    @GetMapping
    public List<PersonalCalendar> getAllEvents() {
        return personalCalendarRepository.findAll();
    }

    // 新增事件
    @PostMapping
    public PersonalCalendar createEvent(@RequestBody PersonalCalendar personalCalendar) {
        return personalCalendarRepository.save(personalCalendar);
    }

    // 刪除事件
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id) {
        personalCalendarRepository.deleteById(id);
    }
}
