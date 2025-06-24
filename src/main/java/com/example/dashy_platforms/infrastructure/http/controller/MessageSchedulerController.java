package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.ScheduleMessage.ScheduleMessageRequest;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import com.example.dashy_platforms.infrastructure.database.service.MessageSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/instagram")
public class MessageSchedulerController {
    @Autowired
    InstagramService instagramService;
    @Autowired
    private MessageSchedulerService schedulerService;

    @PostMapping("/schedule")
    public ResponseEntity<ScheduledMessageEntity> scheduleMessage(@RequestBody ScheduleMessageRequest request) {
        try {
            Set<String> activeUsers = instagramService.getActiveUsers();

            List<ScheduledMessageEntity> scheduledMessage = schedulerService.scheduleMessageForAllActiveUsers(request,activeUsers);
            return ResponseEntity.ok((ScheduledMessageEntity) scheduledMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/schedule-tempalate")
    public ResponseEntity<ScheduledMessageEntity> scheduleMessageTemplate(@RequestBody ScheduleMessageRequest request) {
        try {
            Set<String> activeUsers = instagramService.getActiveUsers();

            List<ScheduledMessageEntity> scheduledMessage = schedulerService.scheduleMessageForAllActiveUsers(request , activeUsers);
            return ResponseEntity.ok((ScheduledMessageEntity) scheduledMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/schedule/{id}/stop")
    public ResponseEntity<Void> stopScheduledMessage(@PathVariable Long id) {
        try {
            schedulerService.stopScheduledMessage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/schedule/recipient/{recipientId}")
    public ResponseEntity<List<ScheduledMessageEntity>> getScheduledMessages(@PathVariable String recipientId) {
        List<ScheduledMessageEntity> messages = schedulerService.getActiveScheduledMessages(recipientId);
        return ResponseEntity.ok(messages);
    }
}