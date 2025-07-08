package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessageEntity, Long> {
    @Query("SELECT COUNT(s) > 0 FROM ScheduledMessageEntity s WHERE s.Schedule_sending_date_type = 'BIRTHDAY'")
    boolean existsGlobalBirthdayConfig();
    List<ScheduledMessageEntity> findByIsActiveTrueAndNextExecutionBefore(LocalDateTime dateTime);
    @Query("SELECT s FROM ScheduledMessageEntity s WHERE s.isActive = true AND s.Schedule_sending_date_type = 'BIRTHDAY'")
    List<ScheduledMessageEntity> findActiveBirthdayMessages();     List<ScheduledMessageEntity> findByRecipientIdAndIsActiveTrue(String recipientId);
}
