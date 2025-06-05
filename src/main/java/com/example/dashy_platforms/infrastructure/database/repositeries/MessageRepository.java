package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByRecipientId(String recipientId);
    List<MessageEntity> findByStatus(String status);

    @Query("SELECT im FROM MessageEntity im WHERE im.status = 'PENDING' ORDER BY im.createdAt ASC")
    List<MessageEntity> findPendingMessages();
}
