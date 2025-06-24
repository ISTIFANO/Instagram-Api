package com.example.dashy_platforms.domaine.model.ScheduleMessage;

import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.enums.ScheduleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleMessageRequest {
    private MessageContent messageContent;
    private ScheduleType scheduleType;
    private Integer intervalValue;
    private IntervalUnit intervalUnit;
    private Integer dayOfWeek;
    private Integer dayOfMonth;
    private Integer hourOfDay;
    private Integer minuteOfHour;
    private Integer maxExecutions;
    private String messageType;
    private LocalDateTime startDate;


}
