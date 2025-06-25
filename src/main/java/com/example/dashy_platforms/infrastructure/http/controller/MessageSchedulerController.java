package com.example.dashy_platforms.infrastructure.http.controller;
import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.ScheduleMessage.ScheduleMessageRequest;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import com.example.dashy_platforms.infrastructure.database.service.MessageSchedulerService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> stopScheduledMessage(@PathVariable Long id) {
        try {
            schedulerService.stopScheduledMessage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok("is stoped ");
        }
    }

    @GetMapping("/schedule/recipient/{recipientId}")
    public ResponseEntity<List<ScheduledMessageEntity>> getScheduledMessages(@PathVariable String recipientId) {
        List<ScheduledMessageEntity> messages = schedulerService.getActiveScheduledMessages(recipientId);
        return ResponseEntity.ok(messages);
    }
    @Operation(summary = "Upload and broadcast media", description = "Upload media and send it to all active users")
    @PostMapping(value = "/upload-media",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Map<String, Object>> uploadAndSendMedia(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "request", required = false) String requestJson) {
        try {

            ScheduleMessageRequest request = new ScheduleMessageRequest();
            if (requestJson != null && !requestJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                request = mapper.readValue(requestJson, ScheduleMessageRequest.class);
            }


            Set<String> activeUsers = instagramService.getActiveUsers();

            String attachmentId = instagramService.uploadMediaAndGetAttachmentId(file);
            String mediaType =this.schedulerService.getMediaType(file.getContentType());
            request.setMediaType(mediaType);
            request.setAttachmentId(attachmentId);
            JsoonFormat jsoonFormat = new JsoonFormat();
            jsoonFormat.printJson(request);
            List<ScheduledMessageEntity> scheduledMessage =schedulerService.scheduleMessageForAllActiveUsers(request, activeUsers);

            return ResponseEntity.ok((Map<String, Object>) scheduledMessage);
        } catch (Exception e) {
            return serverError("Failed to upload and send media: " + e.getMessage());
        }
    }
    private ResponseEntity<Map<String, Object>> serverError(String error) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", error));
    }

    private Map<String, Object> buildStats(Map<String, Boolean> results) {
        long successCount = results.values().stream().filter(Boolean::booleanValue).count();
        return Map.of("totalUsers", results.size(), "successCount", successCount, "failureCount", results.size() - successCount, "details", results
        );
    }
}