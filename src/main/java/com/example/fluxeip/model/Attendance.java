package com.example.fluxeip.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "shift_type_id", nullable = false)
    private ShiftType shiftType;
    
    // 新增 OneToMany 關聯到 AttendanceLogs
    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttendanceLogs> attendanceLogs;

    // 新增 OneToMany 關聯到 AttendanceViolations
    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttendanceViolations> attendanceViolations;
    
    @Column(name = "total_hours")
    private float totalHours;
    
    @Column(name = "regular_hours")
    private float regularHours;
    
    @Column(name = "overtime_hours")
    private float overtimeHours;
    
    @Column(name = "field_work_hours")
    private float fieldWorkHours;
    
    @Column(name = "has_violation")
    private boolean hasViolation;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

	
}
