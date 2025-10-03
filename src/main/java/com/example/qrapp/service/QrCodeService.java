package com.example.qrapp.service;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.repository.QrCodeRepository;
import com.example.qrapp.util.QrCodeGenerator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QrCodeService {

    @Value("${app.qr.default-expiry-days:365}")
    private int defaultExpiryDays;

    @Value("${app.qr.default-max-articles:20}")
    private int defaultMaxArticles;

    @Value("${app.url}")
    private String appUrl;

    private final QrCodeRepository qrCodeRepository;

    private final QrCodeGenerator qrCodeGenerator;

    public QrCode createQrCode(String description, User owner, LocalDateTime expiryDate, Integer maxArticles) {
        if (expiryDate == null) {
            expiryDate = LocalDateTime.now().plusDays(defaultExpiryDays);
        }
        if (maxArticles == null) {
            maxArticles = defaultMaxArticles;
        }

        QrCode qrCode = new QrCode(description, owner, expiryDate, maxArticles);
        return qrCodeRepository.save(qrCode);
    }

    public QrCode createQrCode(String description, User owner) {
        return createQrCode(description, owner, null, null);
    }

    @Transactional(readOnly = true)
    public Optional<QrCode> findByQrId(String qrId) {
        return qrCodeRepository.findByQrId(qrId);
    }

    @Transactional(readOnly = true)
    public Optional<QrCode> findById(UUID id) {
        return qrCodeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<QrCode> findByOwner(User owner) {
        return qrCodeRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    @Transactional(readOnly = true)
    public Page<QrCode> findAll(Pageable pageable, String filter, String search) {
        Page<QrCode> qrCodesPage;

        qrCodesPage = switch (filter != null ? filter.toLowerCase() : "") {
            case "active" -> findActive(pageable);
            case "expired" -> findExpired(pageable);
            case "full" -> findFull(pageable);
            default -> qrCodeRepository.findAll(pageable);
        };

        if (search != null && !search.trim().isEmpty()) {
            List<QrCode> filteredList = qrCodesPage.getContent().stream()
                    .filter(qr -> qr.getDescription().toLowerCase().contains(search.toLowerCase()) ||
                            qr.getOwner().getLastName().toLowerCase().contains(search.toLowerCase()) ||
                            qr.getOwner().getFirstName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());

            return new PageImpl<>(filteredList, pageable, filteredList.size());
        }

        return qrCodesPage;
    }

    @Transactional(readOnly = true)
    public Page<QrCode> findActive(Pageable pageable) {
        return qrCodeRepository.findByExpiryDateAfterOrderByCreatedAtDesc(LocalDateTime.now(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<QrCode> findExpired(Pageable pageable) {
        return qrCodeRepository.findByExpiryDateBeforeOrderByCreatedAtDesc(LocalDateTime.now(), pageable);
    }

    public Page<QrCode> findFull(Pageable pageable) {
        return qrCodeRepository.findByArticlesCountEqualsMaxArticles(pageable);
    }

    public QrCode updateQrCode(QrCode qrCode) {
        return qrCodeRepository.save(qrCode);
    }

    public void deleteQrCode(UUID id) {
        qrCodeRepository.deleteById(id);
    }

    public void deleteQrCode(QrCode qrCode) {
        qrCodeRepository.delete(qrCode);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return qrCodeRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return qrCodeRepository.countByExpiryDateAfter(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long countExpired() {
        return qrCodeRepository.countByExpiryDateBefore(LocalDateTime.now());
    }

    public String generateQrCodeBase64(String qrId, int width, int height, boolean darkMode) {
        String qrUrl = generateQrUrl(qrId);
        return qrCodeGenerator.generateQrCodeBase64(qrUrl, width, height, darkMode);
    }

    private String generateQrUrl(String qrId) {
        return appUrl + qrId;
    }

}