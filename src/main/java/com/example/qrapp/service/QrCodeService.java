package com.example.qrapp.service;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.repository.QrCodeRepository;
import com.example.qrapp.util.QrCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class QrCodeService {

    @Value("${app.qr.default-expiry-days:365}")
    private int defaultExpiryDays;

    @Value("${app.qr.default-max-articles:20}")
    private int defaultMaxArticles;

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
    public Page<QrCode> findAll(Pageable pageable) {
        return qrCodeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<QrCode> findActive() {
        return qrCodeRepository.findByExpiryDateAfterOrderByCreatedAtDesc(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<QrCode> findExpired() {
        return qrCodeRepository.findByExpiryDateBeforeOrderByCreatedAtDesc(LocalDateTime.now());
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

    public byte[] generateQrCodeImage(String qrId, int width, int height) {
        String qrUrl = generateQrUrl(qrId);
        return qrCodeGenerator.generateQrCodeImage(qrUrl, width, height);
    }

    public String generateQrCodeBase64(String qrId, int width, int height) {
        String qrUrl = generateQrUrl(qrId);
        return qrCodeGenerator.generateQrCodeBase64(qrUrl, width, height);
    }

    private String generateQrUrl(String qrId) {
        return "https://yourapp.com/qr/" + qrId;
    }
}