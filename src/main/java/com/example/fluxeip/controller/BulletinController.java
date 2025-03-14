package com.example.fluxeip.controller;

import com.example.fluxeip.model.Bulletin;
import com.example.fluxeip.repository.BulletinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bulletins")
public class BulletinController {

    @Autowired
    private BulletinRepository bulletinRepository;

    // 取得所有公告
    @GetMapping
    public List<Bulletin> getAllBulletins() {
        return bulletinRepository.findAll();
    }

    // 取得單一公告
    @GetMapping("/{id}")
    public ResponseEntity<Bulletin> getBulletinById(@PathVariable Integer id) {
        Optional<Bulletin> bulletin = bulletinRepository.findById(id);
        return bulletin.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 依 statusId 分頁查詢公告
    @GetMapping("/status/{statusId}")
    public Page<Bulletin> getBulletinsByStatus(@PathVariable Integer statusId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return bulletinRepository.findByStatusId(statusId, PageRequest.of(page, size));
    }

    // 新增公告
    @PostMapping
    public ResponseEntity<Bulletin> createBulletin(@RequestBody Bulletin bulletin) {
        Bulletin savedBulletin = bulletinRepository.save(bulletin);
        return ResponseEntity.ok(savedBulletin);
    }

    // 更新公告
    @PutMapping("/{id}")
    public ResponseEntity<Bulletin> updateBulletin(@PathVariable Integer id, @RequestBody Bulletin updatedBulletin) {
        return bulletinRepository.findById(id)
                .map(bulletin -> {
                    bulletin.setTitle(updatedBulletin.getTitle());
                    bulletin.setCreater(updatedBulletin.getCreater());
                    bulletin.setContent(updatedBulletin.getContent());
                    bulletin.setStatusId(updatedBulletin.getStatusId());
                    Bulletin savedBulletin = bulletinRepository.save(bulletin);
                    return ResponseEntity.ok(savedBulletin);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 刪除公告
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBulletin(@PathVariable Integer id) {
        if (bulletinRepository.existsById(id)) {
            bulletinRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
