package com.example.dashy_platforms.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public MessageEntity() {}

    public MessageEntity(String recipientId, String messageContent, String templateName, String messageType) {
        this.recipientId = recipientId;
        this.messageContent = messageContent;
        this.templateName = templateName;
        this.messageType = messageType;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
