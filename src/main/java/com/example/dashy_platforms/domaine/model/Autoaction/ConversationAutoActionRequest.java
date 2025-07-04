package com.example.dashy_platforms.domaine.model.Autoaction;


import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ConversationAutoActionRequest {
    @NotBlank(message = "Response message is required")
    @Size(max = 1000, message = "Response message must be less than 1000 characters")
    private String responseMessage;

    @NotBlank(message = "Message type is required")
    private String messageType;

    @NotNull(message = "Status is required")
    private ConversationAutoAction.ActionStatus status;

    @NotNull(message = "Priority is required")
    @Min(value = 0, message = "Priority must be 0 or greater")
    private Integer priority;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @Valid
    @NotEmpty(message = "At least one trigger is required")
    private List<MessageAutoActionRequest> triggers;
}