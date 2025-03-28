package com.example.fluxeip.dto;

import java.time.LocalDateTime;

import com.example.fluxeip.model.Notify;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotifyResponse {

    private Integer id;
    private String message;
    private LocalDateTime createTime;
    private Boolean isRead;

    private Integer receiveEmployeeId;
    private String receiveEmployeeName;

    public NotifyResponse() {
    }

    public NotifyResponse(Notify notify) {
        this.id = notify.getId();
        this.message = notify.getMessage();
        this.createTime = notify.getCreateTime();
        this.isRead = notify.getIsRead();
        this.receiveEmployeeId = notify.getReceiveEmployeeId();

        if (notify.getReceiveEmployee() != null) {
            this.receiveEmployeeName = notify.getReceiveEmployee().getEmployeeName();
        }
    }
}