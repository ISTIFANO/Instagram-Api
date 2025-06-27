package com.example.dashy_platforms.domaine.model.Webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPayload {
    private String object;
    private List<Entry> entry;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private String id;
        private long time;
        private List<Messaging> messaging;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Messaging {
        private Sender sender;
        private Recipient recipient;
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
    public static class Recipient {
        private String id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String mid;
        private String text;
    }
}
