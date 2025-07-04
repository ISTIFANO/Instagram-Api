package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import com.example.dashy_platforms.infrastructure.database.service.MessageAutoActionService;

import java.util.List;

public interface IMessageAutoAction {

    List<MessageAutoActionService> findByCompanyIdAndStatus(String  companyId, ConversationAutoAction conversationAutoAction);
}
