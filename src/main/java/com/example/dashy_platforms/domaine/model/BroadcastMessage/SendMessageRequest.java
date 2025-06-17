package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendMessageRequest {
    private Recipient recipient;
    private MessageContent message;
    private Attachment attachment;

    @Data
    public static class Recipient {
        private String id;
    }

    @Data
    public static class MessageContent {
        private String text;
    }

    @Data
    public static class Attachment {
        private String type;
        private Payload payload;

        @Data
        public static class Payload {
            @JsonProperty("attachment_id")
            private String attachmentId;
            private String url;
            @JsonProperty("is_reusable")
            private Boolean isReusable;
        }
    }

}