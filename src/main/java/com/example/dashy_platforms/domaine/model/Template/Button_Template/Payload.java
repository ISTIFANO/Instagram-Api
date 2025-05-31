package com.example.dashy_platforms.domaine.model.Template.Button_Template;

import com.example.dashy_platforms.domaine.model.ElementModel;
import com.example.dashy_platforms.domaine.model.TemplateButton;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Payload {
    @JsonProperty("template_type")
    private String templateType = "button";

    @JsonProperty("text")
    private String text;

    @JsonProperty("buttons")
    private List<TemplateButton> elements;
}
