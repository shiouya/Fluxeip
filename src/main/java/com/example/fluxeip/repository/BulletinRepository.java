package com.example.fluxeip.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fluxeip.model.Bulletin;

public interface BulletinRepository extends JpaRepository<Bulletin, Integer> {

    // 模糊查詢標題
    List<Bulletin> findByTitleContaining(String title);

    // 根據 creator 查詢
    List<Bulletin> findByCreater(String creater);

    // 依據 title 和 statusId 查詢
    List<Bulletin> findByTitleContainingAndStatusId(String title, String statusId);

    // 分頁查詢公告（按 statusId）
    Page<Bulletin> findByStatusId(String statusId, Pageable pageable);
}
