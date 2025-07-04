package com.example.dashy_platforms.infrastructure.database.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_auto_actions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConversationAutoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 @Column(name = "response_message", nullable = false, length = 1000)
 private String responseMessage;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionStatus status;

    public enum ActionStatus {
        ACTIVE, INACTIVE, PAUSED
    }
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

}
