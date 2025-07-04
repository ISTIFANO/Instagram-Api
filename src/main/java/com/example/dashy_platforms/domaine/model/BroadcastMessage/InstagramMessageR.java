package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstagramMessageR {
    private Recipient recipient;
    private Message message;

    public InstagramMessageR(String userId) {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recipient {
        private String id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String text;
        private Attachment attachment;
        private List<QuickReply> quick_replies;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Attachment {
            private String type;
            private Payload payload;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Payload {
                private String template_type;
                private String text;
                private List<Element> elements;
                private List<Button> buttons;
                private String attachment_id;

                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Element {
                    private String title;
                    private String image_url;
                    private String subtitle;
                    private DefaultAction default_action;
                    private List<Button> buttons;

                    @Data
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class DefaultAction {
                        private String type;
                        private String url;
                    }
                }

                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Button {
                    private String type;
                    private String url;
                    private String title;
                    private String payload;
                }
            }
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class QuickReply {
            private String content_type = "text";
            private String title;
            private String payload;
        }
    }
}