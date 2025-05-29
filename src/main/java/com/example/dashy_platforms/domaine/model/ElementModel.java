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

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("subtitle")
        private String subtitle;

        @JsonProperty("default_action")
        private DefaultAction defaultAction;

        @JsonProperty("buttons")
        private List<TemplateButton> buttons;


}
