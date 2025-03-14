package com.example.fluxeip.service;

import com.example.fluxeip.model.Bulletin;
import com.example.fluxeip.repository.BulletinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BulletinService {

    @Autowired
    private BulletinRepository bulletinRepository;

    // 取得所有公告
    public List<Bulletin> getAllBulletins() {
        return bulletinRepository.findAll();
    }

    // 取得單一公告
    public Optional<Bulletin> getBulletinById(Integer id) {
        return bulletinRepository.findById(id);
    }

    // 依 statusId 分頁查詢公告
    public Page<Bulletin> getBulletinsByStatus(String statusId, int page, int size) {
        return bulletinRepository.findByStatusId(statusId, PageRequest.of(page, size));
    }

    // 新增公告
    public Bulletin createBulletin(Bulletin bulletin) {
        return bulletinRepository.save(bulletin);
    }

    // 更新公告
    public Optional<Bulletin> updateBulletin(Integer id, Bulletin updatedBulletin) {
        return bulletinRepository.findById(id).map(bulletin -> {
            bulletin.setTitle(updatedBulletin.getTitle());
            bulletin.setCreater(updatedBulletin.getCreater());
            bulletin.setContent(updatedBulletin.getContent());
            bulletin.setStatusId(updatedBulletin.getStatusId());
            return bulletinRepository.save(bulletin);
        });
    }

    // 刪除公告
    public boolean deleteBulletin(Integer id) {
        if (bulletinRepository.existsById(id)) {
            bulletinRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
