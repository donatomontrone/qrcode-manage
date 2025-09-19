package com.example.qrapp.controller;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.service.ArticleService;
import com.example.qrapp.service.QrCodeService;
import com.example.qrapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final QrCodeService qrCodeService;

    private final UserService userService;

    private final ArticleService articleService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalQrCodes", qrCodeService.countAll());
        stats.put("activeQrCodes", qrCodeService.countActive());
        stats.put("expiredQrCodes", qrCodeService.countExpired());
        stats.put("totalUsers", userService.countAll());
        stats.put("totalArticles", articleService.countAll());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/qr/{qrId}/status")
    public ResponseEntity<Map<String, Object>> getQrStatus(@PathVariable String qrId) {
        try {
            QrCode qrCode = qrCodeService.findByQrId(qrId)
                    .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

            Map<String, Object> status = new HashMap<>();
            status.put("qrId", qrCode.getQrId());
            status.put("description", qrCode.getDescription());
            status.put("status", qrCode.getStatus());
            status.put("isExpired", qrCode.isExpired());
            status.put("canAddArticle", qrCode.canAddArticle());
            status.put("currentArticles", qrCode.getArticles().size());
            status.put("maxArticles", qrCode.getMaxArticles());
            status.put("expiryDate", qrCode.getExpiryDate());

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/qr/{qrId}/validate")
    public ResponseEntity<Map<String, Object>> validateQrCode(@PathVariable String qrId) {
        try {
            QrCode qrCode = qrCodeService.findByQrId(qrId)
                    .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", true);
            validation.put("active", !qrCode.isExpired());
            validation.put("canAddArticles", qrCode.canAddArticle());
            validation.put("message", qrCode.isExpired() ? "QR Code scaduto" : "QR Code valido");

            return ResponseEntity.ok(validation);

        } catch (Exception e) {
            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", false);
            validation.put("active", false);
            validation.put("canAddArticles", false);
            validation.put("message", "QR Code non trovato");

            return ResponseEntity.ok(validation);
        }
    }
}