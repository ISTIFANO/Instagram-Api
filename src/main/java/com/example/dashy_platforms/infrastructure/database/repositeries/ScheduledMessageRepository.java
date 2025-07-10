package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(s) FROM ScheduledMessageEntity s WHERE s.company.id = :companyId")
    long countAllByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(s) FROM ScheduledMessageEntity s WHERE s.isActive = true AND s.company.id = :companyId")
    long countActiveByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(s) FROM ScheduledMessageEntity s WHERE s.isActive = false AND s.company.id = :companyId")
    long countInactiveByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(s) FROM ScheduledMessageEntity s WHERE s.executionCount > 0 AND s.company.id = :companyId")
    long countExecutedByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT s.scheduleType, COUNT(s) FROM ScheduledMessageEntity s WHERE s.company.id = :companyId GROUP BY s.scheduleType")
    List<Object[]> countByScheduleTypeByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT s.Schedule_sending_date_type, COUNT(s) FROM ScheduledMessageEntity s WHERE s.company.id = :companyId GROUP BY s.Schedule_sending_date_type")
    List<Object[]> countByScheduleDateTypeByCompanyId(@Param("companyId") Long companyId);
}
