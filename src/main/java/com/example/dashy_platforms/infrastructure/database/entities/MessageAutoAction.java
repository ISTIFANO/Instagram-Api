package com.example.dashy_platforms.infrastructure.database.entities;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_auto_action")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageAutoAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trigger_keyword", nullable = false)
    private String triggerKeyword;

    @Column(name = "match_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchType matchType;

    // Case sensitive matching
    @Column(name = "case_sensitive", nullable = false)
    private Boolean caseSensitive = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conversation_auto_action_id", nullable = false)
    private ConversationAutoAction conversationAutoAction;

    public enum MatchType {
        EXACT, CONTAINS, STARTS_WITH, ENDS_WITH
    }
}
