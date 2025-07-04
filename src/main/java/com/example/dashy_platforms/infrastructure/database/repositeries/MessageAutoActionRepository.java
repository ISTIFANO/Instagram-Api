package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import com.example.dashy_platforms.infrastructure.database.entities.MessageAutoAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageAutoActionRepository extends JpaRepository<MessageAutoAction, Long> {
    List<MessageAutoAction> findByConversationAutoActionCompanyIdAndConversationAutoActionStatus(
            Long companyId, ConversationAutoAction.ActionStatus status);

    List<MessageAutoAction> findByConversationAutoActionId(Long conversationAutoActionId);
    void deleteByConversationAutoActionId(Long conversationAutoActionId);
}
