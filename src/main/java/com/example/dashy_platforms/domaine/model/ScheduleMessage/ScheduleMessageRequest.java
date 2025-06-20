package com.example.dashy_platforms.domaine.model.ScheduleMessage;

import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.enums.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class ScheduleMessageRequest {
    private String recipientId;
    private String messageContent;
    private ScheduleType scheduleType;
    private Integer intervalValue;
    private IntervalUnit intervalUnit;
    private Integer dayOfWeek;
    private Integer dayOfMonth;
    private Integer hourOfDay;
    private Integer minuteOfHour;
    private Integer maxExecutions;
    private LocalDateTime startDate;

    public ScheduleMessageRequest(){

    }
}
