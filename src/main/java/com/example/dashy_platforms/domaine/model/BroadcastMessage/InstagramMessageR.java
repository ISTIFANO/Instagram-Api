package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import lombok.Data;

import java.util.List;
@Data
public class InstagramMessageR {
    private Recipient recipient;
    private Message message;

    @Data
    public static class Recipient {
        private String id;
    }

    @Data
    public static class Message {
        private String text;
        private Attachment attachment;
        private List<QuickReply> quick_replies;

        @Data
        public static class Attachment {
            private String type;
            private Payload payload;

            @Data
            public static class Payload {
                private String template_type;
                private String text;
                private List<Element> elements;
                private List<Button> buttons;
                private String attachment_id;

                @Data
                public static class Element {
                    private String title;
                    private String image_url;
                    private String subtitle;
                    private DefaultAction default_action;
                    private List<Button> buttons;

                    @Data
                    public static class DefaultAction {
                        private String type;
                        private String url;
                    }
                }

                @Data
                public static class Button {
                    private String type;
                    private String url;
                    private String title;
                    private String payload;
                }
            }
        }

        @Data
        public static class QuickReply {
            private String content_type;
            private String title;
            private String payload;
        }
    }
}
