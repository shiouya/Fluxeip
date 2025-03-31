package com.example.fluxeip.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "field_work_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldWorkRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 外勤記錄ID

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;  // 員工

    @Column(name = "field_work_date", nullable = false)
    private LocalDate fieldWorkDate;  // 外勤日期

    @Column(name = "total_hours", nullable = false, precision = 3, scale = 2)
    private BigDecimal totalHours;  // 總外勤工時

    @Column(name = "location", nullable = false, length = 255)
    private String location;  // 外勤地點

    @Column(name = "purpose", nullable = false, length = 255)
    private String purpose;  // 外勤目的

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 創建時間（默認當前時間）

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;  // 狀態
}
