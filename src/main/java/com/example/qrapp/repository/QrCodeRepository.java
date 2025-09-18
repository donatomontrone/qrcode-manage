package com.example.qrapp.repository;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    Optional<QrCode> findByQrId(String qrId);

    List<QrCode> findByOwnerOrderByCreatedAtDesc(User owner);

    Page<QrCode> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<QrCode> findByExpiryDateAfterOrderByCreatedAtDesc(LocalDateTime date);

    List<QrCode> findByExpiryDateBeforeOrderByCreatedAtDesc(LocalDateTime date);

    long countByExpiryDateAfter(LocalDateTime date);

    long countByExpiryDateBefore(LocalDateTime date);

    @Query("SELECT q FROM QrCode q WHERE q.owner = ?1 AND q.expiryDate > ?2")
    List<QrCode> findActiveQrCodesByOwner(User owner, LocalDateTime currentDate);
}