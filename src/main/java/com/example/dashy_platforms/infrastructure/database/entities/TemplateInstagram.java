package com.example.dashy_platforms.infrastructure.database.entities;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "template_instagram")
@Data
public class TemplateInstagram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "lang")
    private String lang;

    @Column(name = "template_content", columnDefinition = "TEXT")
    private String templateContent;

    @Column(name = "json_options", columnDefinition = "TEXT")
    private String jsonOptions;
// pour identifier la template
     @Column(name = "code", unique = true)

    private String code;

    @Column(name = "archived")
    private Boolean archived =false;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "status")
    private String status;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "template_type")
    private String templateType ="GENERIC";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}