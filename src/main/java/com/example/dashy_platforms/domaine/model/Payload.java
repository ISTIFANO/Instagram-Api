package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.Element;
import java.util.List;
@Getter
@Setter
@Data
public class Payload {

    @JsonProperty("template_type")
    private String templateType = "generic";

    @JsonProperty("elements")
    private List<ElementModel> elements;
}
