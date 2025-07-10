package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.Stats.InstagramStats;
import com.example.dashy_platforms.domaine.model.UserListInfoResponse;
import com.example.dashy_platforms.domaine.service.IScheduledMessageExecutorService;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/instagram")
@Slf4j
public class InstagramStatsController {

    @Autowired
    InstagramService instagramService;
    @Autowired
    IScheduledMessageExecutorService scheduledMessageExecutorService;
    @GetMapping("scheduledStats")
    public ResponseEntity<Map<String, Object>> getBroadcastStatsByCompany(
            @RequestParam("companyId") Long companyId) {

        Map<String, Object> stats = scheduledMessageExecutorService.getBroadcastStatsByCompanyId(companyId);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/filter")
    public ResponseEntity<InstagramStats> getInstagramStatsWithDate(
            @RequestParam("companyId") Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }
        Set<UserListInfoResponse> users = instagramService.listMessagedUsers();
        int totalContact = users.size();

        Map<String, Long> statusStats = instagramService.getMessageStatsByCompanyAndDate(companyId, date);

        Set<String> activeUsers = instagramService.getActiveUsers();

        InstagramStats stats = new InstagramStats();
        stats.setTotalcontact(totalContact);
        stats.setActiveusers(activeUsers.size());
        stats.setReceived(statusStats.getOrDefault("RECEIVED", 0L));
        stats.setFailed(statusStats.getOrDefault("FAILED", 0L));
        stats.setPending(statusStats.getOrDefault("PENDING", 0L));
        stats.setSent(statusStats.getOrDefault("SENT", 0L));

        return ResponseEntity.ok(stats);
    }


    @GetMapping("/stats")
    public ResponseEntity<InstagramStats> getInstagramStats(@RequestParam("companyId") Long companyId) {

        Set<UserListInfoResponse> users = instagramService.listMessagedUsers();
        int totalContact = users.size();

        Map<String, Long> statusStats = instagramService.getMessageStatusStats(companyId);
        Set<String> activeusers = instagramService.getActiveUsers();

        InstagramStats stats = new InstagramStats();
        stats.setActiveusers(activeusers.size());
        stats.setTotalcontact(totalContact);
        stats.setReceived(statusStats.getOrDefault("RECEIVED", 0L));
        stats.setFailed(statusStats.getOrDefault("FAILED", 0L));
        stats.setPending(statusStats.getOrDefault("PENDING", 0L));
        stats.setSent(statusStats.getOrDefault("SENT", 0L));

        return ResponseEntity.ok(stats);
    }

}
