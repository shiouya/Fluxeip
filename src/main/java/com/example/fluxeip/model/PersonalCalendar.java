package com.example.fluxeip.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "personal_calendar") // 表名可以根據實際需求修改
public class PersonalCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 事件ID

    @Column(name = "content", nullable = false)  // content 代替 title
    private String content;  // 事件內容

    @Column(name = "start_date", nullable = false)  // 開始時間
    private LocalDateTime startDate;

    @Column(name = "finish_date", nullable = false)  // 結束時間
    private LocalDateTime finishDate;

    @Column(name = "created_at", nullable = false, updatable = false)  // 創建時間
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // 當事件創建時，設置創建時間
    }
}
