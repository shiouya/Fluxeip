package com.example.fluxeip.model;

import java.time.LocalDateTime;

public interface Request {
    Integer getId();
    Employee getEmployee();
    Status getStatus();
    LocalDateTime getSubmittedAt();
}
