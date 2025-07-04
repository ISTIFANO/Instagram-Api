package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageTemplate {

    private String type;
    private Object content;
    private String caption;
    public MessageTemplate() {}
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