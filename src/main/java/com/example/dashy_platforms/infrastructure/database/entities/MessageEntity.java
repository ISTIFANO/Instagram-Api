package com.example.dashy_platforms.infrastructure.database.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "sender_id",nullable = true)
    private String senderId;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    @Column(name = "message_id", unique = true,nullable = true)
    private String messageId;
    @Column(name = "reaction")
    private String reaction;
    public MessageEntity() {}

    public MessageEntity(String recipientId, String messageContent, String messageType) {
        this.recipientId = recipientId;
        this.messageContent = messageContent;
        this.messageType = messageType;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
