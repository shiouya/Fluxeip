package com.example.fluxeip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RequestIdGenerator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getNextRequestId() {
        String sql = "SELECT MAX(id) FROM (" +
                     "SELECT id FROM leave_requests " +
                     "UNION ALL " +
                     "SELECT id FROM missing_punch_requests " +
                     "UNION ALL " +
                     "SELECT id FROM expense_requests " +
                     "UNION ALL " +
                     "SELECT id FROM work_adjustment_requests " +
                     ") AS all_requests";

        Integer maxId = jdbcTemplate.queryForObject(sql, Integer.class);
        return (maxId == null) ? 1 : maxId + 1;
    }
}
