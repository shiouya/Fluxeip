package com.example.fluxeip.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCalendarRequestDTO {

    private Integer departmentId;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private String content;

    // ✅ 自定義驗證邏輯
    public boolean isValid() {
        // 檢查 content 是否為空
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        // 檢查 departmentId 是否為 null
        if (departmentId == null) {
            return false;
        }

        // 檢查 startDate 和 finishDate 是否為未來或當前時間
        LocalDateTime now = LocalDateTime.now();
        if (startDate == null || finishDate == null) {
            return false;
        }

        if (startDate.isBefore(now) || finishDate.isBefore(now)) {
            return false;
        }

        // 檢查 finishDate 是否晚於 startDate
        if (finishDate.isBefore(startDate)) {
            return false;
        }

        return true;
    }

    // ✅ 返回錯誤信息
    public String getValidationErrorMessage() {
        if (content == null || content.trim().isEmpty()) {
            return "Content cannot be empty.";
        }

        if (departmentId == null) {
            return "Department ID cannot be null.";
        }

        LocalDateTime now = LocalDateTime.now();

        if (startDate == null || finishDate == null) {
            return "Start date and finish date cannot be null.";
        }

        if (startDate.isBefore(now) || finishDate.isBefore(now)) {
            return "Start date and finish date must be in the future or at the current time.";
        }

        if (finishDate.isBefore(startDate)) {
            return "Finish date must be after start date.";
        }

        return null; // 沒有錯誤
    }
}

