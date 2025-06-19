package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class SendMessageRequest {
    private Recipient recipient;
    private MessageContent message;
    private Attachment attachment;

    public SendMessageRequest() {
    }
@AllArgsConstructor
    @Data
    public static class Recipient {
        private String id;
    }
@AllArgsConstructor
    @Data
    public static class MessageContent {
        private String text;
    }
   @AllArgsConstructor
    @Data
    public static class Attachment {
        public  Attachment() {
        }
        private String type;
        private Payload payload;
@AllArgsConstructor
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

