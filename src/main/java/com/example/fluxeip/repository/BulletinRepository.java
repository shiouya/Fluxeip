package com.example.fluxeip.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.Bulletin;

public interface BulletinRepository extends JpaRepository<Bulletin, Integer> {

    // 模糊查詢標題
    List<Bulletin> findByTitleContaining(String title);

    // 根據 statusId 查詢
    List<Bulletin> findByStatusId(Integer statusId);

    // 根據 creator 查詢
    List<Bulletin> findByCreater(String creater);

    // 依據 title 和 statusId 查詢
    @Query("SELECT b FROM Bulletin b WHERE b.title LIKE %:title% AND b.statusId = :statusId")
    List<Bulletin> searchByTitleAndStatus(@Param("title") String title, @Param("statusId") Integer statusId);

    // 分頁查詢
    Page<Bulletin> findByStatusId(Integer statusId, Pageable pageable);
}

