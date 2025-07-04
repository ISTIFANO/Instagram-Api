package com.example.dashy_platforms.infrastructure.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "instagram_users")
public class InstagramUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instagram_user_id", unique = true)
    private String instagramUserId;

    @Column(name = "access_token", length = 1000)
    private String accessToken;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "account_type")
    private String accountType; // PERSONAL, BUSINESS

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "followers_count")
    private Long followersCount;

    @Column(name = "following_count")
    private Long followingCount;

    @Column(name = "media_count")
    private Long mediaCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstagramUserStatus status = InstagramUserStatus.ACTIVE;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


    public enum InstagramUserStatus {
        ACTIVE, INACTIVE, EXPIRED, REVOKED
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
