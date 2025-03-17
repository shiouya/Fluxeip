package com.example.fluxeip.controller;

import com.example.fluxeip.model.Bulletin;
import com.example.fluxeip.repository.BulletinRepository;
import com.example.fluxeip.service.BulletinService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bulletin")
@CrossOrigin(origins = "http://localhost:5173") // 允許 Vue 前端訪問
public class BulletinController {

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private BulletinService bulletinService;
    
    // 取得所有公告
    @GetMapping
    public List<Bulletin> getAllBulletins() {
        return bulletinRepository.findAll();
    }

    // 取得單一公告
    @GetMapping("/{id}")
    public ResponseEntity<Bulletin> getBulletinById(@PathVariable Integer id) {
        Optional<Bulletin> bulletin = bulletinService.getBulletinById(id); // ✅ 正確：使用實例
        return bulletin.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 依 statusId 分頁查詢公告
    @GetMapping("/status/{statusId}")
    public Page<Bulletin> getBulletinsByStatus(@PathVariable String statusId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return bulletinRepository.findByStatusId(statusId, PageRequest.of(page, size));
    }

    // 新增公告
    @PostMapping("/create")
    public ResponseEntity<Bulletin> createBulletin(@RequestBody Bulletin bulletin) {
        bulletin.setCreatedAt(LocalDateTime.now()); // 確保 createdAt 有值
        Bulletin savedBulletin = bulletinRepository.save(bulletin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBulletin);
    }

    // 更新公告
    @PutMapping("/update/{id}")
    public ResponseEntity<Bulletin> updateBulletin(@PathVariable Integer id, @RequestBody Bulletin updatedBulletin) {
        return bulletinRepository.findById(id)
                .map(bulletin -> {
                    if (updatedBulletin.getTitle() != null) {
                        bulletin.setTitle(updatedBulletin.getTitle());
                    }
                    if (updatedBulletin.getCreater() != null) {
                        bulletin.setCreater(updatedBulletin.getCreater());
                    }
                    if (updatedBulletin.getContent() != null) {
                        bulletin.setContent(updatedBulletin.getContent());
                    }
                    if (updatedBulletin.getStatusId() != null) {
                        bulletin.setStatusId(updatedBulletin.getStatusId());
                    }
                    Bulletin savedBulletin = bulletinRepository.save(bulletin);
                    return ResponseEntity.ok(savedBulletin);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 刪除公告
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteBulletin(@PathVariable Integer id) {
        if (bulletinRepository.existsById(id)) {
            bulletinRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

