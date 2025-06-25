package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.entities.TemplateInstagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByRecipientId(String recipientId);
    List<MessageEntity> findByStatus(String status);

    @Query("SELECT im FROM MessageEntity im WHERE im.status = 'PENDING' ORDER BY im.createdAt ASC")
    List<MessageEntity> findPendingMessages();

    @Query("SELECT m FROM MessageEntity m WHERE m.messageContent = :message_content")
    Optional<MessageEntity> findByMessageContent(@Param("message_content") String messageContent);

}
