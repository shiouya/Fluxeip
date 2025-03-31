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

    @Column(name = "employee_id", nullable = false)  // 新增 employeeId 欄位
    private String employeeId;  // 員工ID

    // 如果有需要自動設置 employeeId 的邏輯，可以在這裡處理
    @PrePersist
    public void prePersist() {
        // 如果你需要在事件創建之前自動設置員工ID，可以在這裡進行處理
        // 這裡假設你會從會話中獲取當前的員工ID
        // 比如: this.employeeId = getCurrentEmployeeId();
    }
}

