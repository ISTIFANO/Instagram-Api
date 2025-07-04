package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationAutoActionRepository extends JpaRepository<ConversationAutoAction, Long> {

    List<ConversationAutoAction> findByCompanyIdAndStatus(Long companyId, ConversationAutoAction.ActionStatus status);
    List<ConversationAutoAction> findByCompanyId(Long companyId);

}
