package com.example.fluxeip.dto;

import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DepartmentCalendarDTO {

    private Integer id;  // 事件 ID
    private Integer departmentId;  // 部門 ID
    private LocalDateTime startDate;  // 開始時間
    private LocalDateTime finishDate;  // 結束時間
    private String content;  // 事件內容
}
