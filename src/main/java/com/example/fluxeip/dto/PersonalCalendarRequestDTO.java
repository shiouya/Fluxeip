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
public class PersonalCalendarRequestDTO {

    private String content;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private String employeeId;  // 新增 employeeId 欄位

    // 自定義驗證邏輯
    public boolean isValid() {
        // 驗證 content 是否為 null 或空
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        // 驗證 employeeId 是否為 null 或空
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return false;
        }

        // 驗證 startDate 和 finishDate 是否為未來或當前時間
        LocalDateTime now = LocalDateTime.now();
        if (startDate == null || finishDate == null) {
            return false;
        }

        if (startDate.isBefore(now) || finishDate.isBefore(now)) {
            return false;
        }

        // 驗證 finishDate 是否在 startDate 之後
        if (finishDate.isBefore(startDate)) {
            return false;
        }

        return true;
    }

    // 返回錯誤信息
    public String getValidationErrorMessage() {
        // 驗證 content 是否為空
        if (content == null || content.trim().isEmpty()) {
            return "Content cannot be empty.";
        }

        // 驗證 employeeId 是否為空
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return "Employee ID cannot be empty.";
        }

        LocalDateTime now = LocalDateTime.now();

        // 驗證 startDate 和 finishDate 是否為未來或當前時間
        if (startDate == null || finishDate == null) {
            return "Start date and finish date cannot be null.";
        }

        if (startDate.isBefore(now) || finishDate.isBefore(now)) {
            return "Start date and finish date must be in the future or at the current time.";
        }

        // 驗證 finishDate 是否在 startDate 之後
        if (finishDate.isBefore(startDate)) {
            return "Finish date must be after start date.";
        }

        return null; // 沒有錯誤
    }
}

