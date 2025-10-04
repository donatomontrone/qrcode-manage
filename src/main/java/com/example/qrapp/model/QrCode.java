package com.example.qrapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.qrapp.constants.Constant.*;

@Entity
@Table(name = "qr_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String qrId;

    @Size(max = 255, message = "La descrizione se presente non deve essere superiore ai 255 caratteri")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Future(message = "La data di scadenza del QR code deve essere successiva a oggi")
    @Column(name = "expiry_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiryDate;

    @Positive(message = "Il numero di articoli totali deve essere maggiore di ZERO")
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