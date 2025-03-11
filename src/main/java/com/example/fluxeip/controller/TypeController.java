package com.example.fluxeip.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fluxeip.model.Type;
import com.example.fluxeip.service.TypeService;

@RestController
@RequestMapping("/api/types")
@CrossOrigin
public class TypeController {

    @Autowired
    private TypeService typeService;

    // 獲取所有類型
    @GetMapping
    public List<Type> getAllTypes() {
        return typeService.getAllTypes();
    }

    // 根據分類獲取類型
    @GetMapping("/category/{category}")
    public List<Type> getTypesByCategory(@PathVariable String category) {
        return typeService.getTypesByCategory(category);
    }

    // 根據 ID 獲取類型
    @GetMapping("/{id}")
    public Optional<Type> getTypeById(@PathVariable Integer id) {
        return typeService.getTypeById(id);
    }

    // 新增類型
    @PostMapping
    public Type createType(@RequestBody Type type) {
        return typeService.createType(type);
    }

    // 更新類型
    @PutMapping("/{id}")
    public Type updateType(@PathVariable Integer id, @RequestBody Type type) {
        return typeService.updateType(id, type);
    }

    // 刪除類型
    @DeleteMapping("/{id}")
    public void deleteType(@PathVariable Integer id) {
        typeService.deleteType(id);
    }
}

