package com.example.dashy_platforms.infrastructure.database.entities;

import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.enums.ScheduleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "scheduled_messages")
public class ScheduledMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String recipientId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(nullable = false)
    private String messagetype = "TEXT";

    @Column(nullable = false)
    private String attachment= "Has no media";

    @Column(nullable = false)
    private String mediaType = "Has no media";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "interval_value")
    private Integer intervalValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_unit")
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

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "max_executions")
    private Integer maxExecutions;

    @Column(name = "execution_count", nullable = false)
    private Integer executionCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

}
