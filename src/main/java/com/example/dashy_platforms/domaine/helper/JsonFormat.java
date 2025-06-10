package com.example.dashy_platforms.domaine.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFormat {
    private final ObjectMapper objectMapper;

    public JsonFormat() {
        this.objectMapper = new ObjectMapper();
    }

    public void printJson(Object messageRequest) {
        try {
            String requestJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(messageRequest);
            System.out.println("Request Body (JSON):\n" + requestJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
