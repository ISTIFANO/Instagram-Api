package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.entities.TemplateInstagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByRecipientId(String recipientId);
    List<MessageEntity> findByStatus(String status);
    Optional<MessageEntity> findByMessageId(String messageId);

    @Query("SELECT m.status, COUNT(m) FROM MessageEntity m WHERE m.company.id = :companyId GROUP BY m.status")
    List<Object[]> countMessagesByStatusAndCompany(@Param("companyId") Long companyId);

    @Query("SELECT m.status, COUNT(m) " +
            "FROM MessageEntity m " +
            "WHERE m.company.id = :companyId " +
            "AND m.createdAt >= :startOfDay AND m.createdAt < :endOfDay " +
            "GROUP BY m.status")
    List<Object[]> countMessagesByStatusAndCompanyAndDayRange(
            @Param("companyId") Long companyId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);





    @Query("SELECT m FROM MessageEntity m WHERE m.messageContent = :message_content")
    Optional<MessageEntity> findByMessageContent(@Param("message_content") String messageContent);
    @Query("SELECT m.status, COUNT(m) FROM MessageEntity m GROUP BY m.status")
    List<Object[]> countMessagesByStatus();

}
