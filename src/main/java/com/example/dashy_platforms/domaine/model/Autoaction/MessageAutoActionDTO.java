package com.example.dashy_platforms.domaine.model.Autoaction;

import com.example.dashy_platforms.infrastructure.database.entities.MessageAutoAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAutoActionDTO {
    private Long id;
    private String triggerKeyword;
    private MessageAutoAction.MatchType matchType;
    private Boolean caseSensitive;
    private Long conversationAutoActionId;
}