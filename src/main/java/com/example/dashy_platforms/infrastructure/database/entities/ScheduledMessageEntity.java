package com.example.dashy_platforms.infrastructure.database.entities;

import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@Entity
@Table(name = "scheduled_messages")
public class ScheduledMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String recipientId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(name = "interval_value")
    private Integer intervalValue;

    @Column(name = "interval_unit")
    @Enumerated(EnumType.STRING)
    private IntervalUnit intervalUnit;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    @Column(name = "hour_of_day")
    private Integer hourOfDay;

    @Column(name = "minute_of_hour")
    private Integer minuteOfHour;

    @Column(name = "next_execution")
    private LocalDateTime nextExecution;

    @Column(name = "last_execution")
    private LocalDateTime lastExecution;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "max_executions")
    private Integer maxExecutions;

    @Column(name = "execution_count")
    private Integer executionCount = 0;

    public ScheduledMessageEntity() {
        this.createdAt = LocalDateTime.now();
    }

}