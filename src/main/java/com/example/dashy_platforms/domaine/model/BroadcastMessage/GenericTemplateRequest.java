package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericTemplateRequest {
    private String caption;
    private List<MessageTemplate.GenericContent.Element> elements;
}
