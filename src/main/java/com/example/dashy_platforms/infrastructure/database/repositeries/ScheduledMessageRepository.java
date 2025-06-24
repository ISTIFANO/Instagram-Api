package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessageEntity, Long> {

    List<ScheduledMessageEntity> findByIsActiveTrueAndNextExecutionBefore(LocalDateTime dateTime);

    List<ScheduledMessageEntity> findByRecipientIdAndIsActiveTrue(String recipientId);
}
