package com.example.dashy_platforms.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "instagram_users")
public class InstagramUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "instagram_user_id", unique = true)
    private String instagramUserId;

    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "permissions")
    private String permissions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public InstagramUserEntity() {}

    public InstagramUserEntity(String instagramUserId, String accessToken,
                         LocalDateTime tokenExpiresAt, String permissions) {
        this.instagramUserId = instagramUserId;
        this.accessToken = accessToken;
        this.tokenExpiresAt = tokenExpiresAt;
        this.permissions = permissions;
    }

}
