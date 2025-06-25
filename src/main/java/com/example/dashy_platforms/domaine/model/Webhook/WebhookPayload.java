package com.example.dashy_platforms.domaine.model.Webhook;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookPayload {
    private List<Entry> entry;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry {
        private List<Messaging> messaging;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Messaging {
        private Sender sender;
        private Message message;
        private long timestamp;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sender {
        private String id;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String text;
    }
}

