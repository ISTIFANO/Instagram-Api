package com.example.dashy_platforms.infrastructure.database.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@ToString(exclude = {"users", "templates", "messages", "scheduledMessages"})
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "work_start_time", nullable = false)
    private LocalTime workStartTime;


    @Column(name = "work_end_time", nullable = false)
    private LocalTime workEndTime;

    @Column(name = "instagram_business_account", length = 100)
    private String instagramBusinessAccount;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "ig_scoped__id", nullable = true)
    private String igScopedId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CompanyStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstagramUserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateInstagram> templates = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Autoaction> autoactions;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduledMessageEntity> scheduledMessages = new ArrayList<>();

    public enum CompanyStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    public boolean isWorkingTime(LocalTime time) {
        return !time.isBefore(workStartTime) && !time.isAfter(workEndTime);
    }

    // Helper methods for bidirectional relationship management
    public void addTemplate(TemplateInstagram template) {
        templates.add(template);
        template.setCompany(this);
    }

    public void removeTemplate(TemplateInstagram template) {
        templates.remove(template);
        template.setCompany(null);
    }

    public void addUser(InstagramUserEntity user) {
        users.add(user);
        user.setCompany(this);
    }

    public void addMessage(MessageEntity message) {
        messages.add(message);
        message.setCompany(this);
    }

    public void addScheduledMessage(ScheduledMessageEntity scheduledMessage) {
        scheduledMessages.add(scheduledMessage);
        scheduledMessage.setCompany(this);
    }
}