package com.example.qrapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.qrapp.constants.Constant.*;

@Entity
@Table(name = "qr_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String qrId;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotNull
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @NotNull
    @Positive
    @Column(name = "max_articles")
    private Integer maxArticles;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "qrCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles;

    public QrCode(String description, User owner, LocalDateTime expiryDate, Integer maxArticles) {
        this();
        this.description = description;
        this.owner = owner;
        this.expiryDate = expiryDate;
        this.maxArticles = maxArticles;
    }

    // Business methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean hasReachedMaxArticles() {
        return articles.size() >= maxArticles;
    }

    public boolean canAddArticle() {
        return !isExpired() && !hasReachedMaxArticles();
    }

    public String getStatus() {
        if (isExpired()) {
            return EXPIRED.toString();
        } else if (hasReachedMaxArticles()) {
            return FULL.toString();
        } else {
            return ACTIVE.toString();
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}