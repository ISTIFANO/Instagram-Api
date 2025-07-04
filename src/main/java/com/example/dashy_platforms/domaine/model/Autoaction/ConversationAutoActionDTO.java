package com.example.dashy_platforms.domaine.model.Autoaction;

import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationAutoActionDTO {
    private Long id;
    private String responseMessage;
    private String messageType;
    private ConversationAutoAction.ActionStatus status;
    private Integer priority;
    private Long companyId;
    private List<MessageAutoActionDTO> triggers;
}
