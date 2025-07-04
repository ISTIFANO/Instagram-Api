package com.example.dashy_platforms.domaine.model.Autoaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConversationAutoActionResponse{
    private Long id;
    private String message;
    private LocalDateTime timestamp;
}