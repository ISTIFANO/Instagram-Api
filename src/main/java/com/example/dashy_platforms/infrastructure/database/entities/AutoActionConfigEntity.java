package com.example.dashy_platforms.infrastructure.database.entities;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auto_config_message")
public class AutoActionConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "message_type", nullable = false)
    private String messageType;


    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.STRING)
    private ConversationAutoAction.ActionStatus status;

    public enum ActionStatus {
        ACTIVE, INACTIVE, PAUSED
    }
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


}
