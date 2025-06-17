package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class ElementModel {

        @JsonProperty("title")
        private String title;

        @JsonProperty("attachment_id")
        private String attachment_id;

        @JsonProperty("subtitle")
        private String subtitle;

        @JsonProperty("default_action")
        private DefaultAction defaultAction;

        @JsonProperty("buttons")
        private List<TemplateButton> buttons;

        public void setAttachment_id(String attachment_id) {
                this.attachment_id = attachment_id;
        }

        public String getAttachment_id() {
                return attachment_id;
        }

}
