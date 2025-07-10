package com.example.dashy_platforms.domaine.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public interface IScheduledMessageExecutorService {

    /**
     * Executes scheduled messages that are due for execution.
     * Runs every minute (60000 ms) as a scheduled task.
     */
    @Scheduled(fixedRate = 60000)
    void executeScheduledMessages();

    /**
     * Retrieves broadcast statistics for a given company
     * @param companyId The ID of the company
     * @return Map containing various statistics about scheduled messages
     */
    Map<String, Object> getBroadcastStatsByCompanyId(Long companyId);

    /**
     * Checks for birthday messages and sends them to users whose birthday is today.
     * Runs daily (86400000 ms) as a scheduled task.
     */
    @Scheduled(fixedRate = 86400000)
    void shouldhappybirthday();
}