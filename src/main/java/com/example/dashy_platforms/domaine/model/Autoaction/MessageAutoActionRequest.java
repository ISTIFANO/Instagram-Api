package com.example.dashy_platforms.domaine.model.Autoaction;

import com.example.dashy_platforms.infrastructure.database.entities.MessageAutoAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageAutoActionRequest {
    @NotBlank(message = "Trigger keyword is required")
    private String triggerKeyword;

    @NotNull(message = "Match type is required")
    private MessageAutoAction.MatchType matchType;

    @NotNull(message = "Case sensitivity flag is required")
    private Boolean caseSensitive;
}
