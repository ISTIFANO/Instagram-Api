package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import lombok.Data;

import java.util.List;

@Data
public class MessageTemplate {
    private String type;
    private Object content;
    private String caption;

    @Data
    public static class GenericContent {
        private List<Element> elements;

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
    }

    @Data
    public static class Button {
        private String type;
        private String url;
        private String title;
        private String payload;
    }
}