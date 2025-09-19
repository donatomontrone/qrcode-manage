package com.example.qrapp.controller;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.service.QrCodeService;
import com.example.qrapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/qr")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    private final UserService userService;

    @PostMapping("/create")
    public String createQrCode(@RequestParam String ownerEmail,
                              @RequestParam String description,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiryDate,
                              @RequestParam Integer maxArticles,
                              RedirectAttributes redirectAttributes) {
        try {
            // Trova l'utente proprietario
            User owner = userService.findByEmail(ownerEmail)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + ownerEmail));

            // Crea il QR code
            QrCode qrCode = qrCodeService.createQrCode(description, owner, expiryDate, maxArticles);

            redirectAttributes.addFlashAttribute("successMessage", 
                "QR Code creato con successo! ID: " + qrCode.getQrId());

            return "redirect:/admin/qr/" + qrCode.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Errore durante la creazione del QR Code: " + e.getMessage());
            return "redirect:/admin/qr/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editQrCode(@PathVariable UUID id, Model model) {
        QrCode qrCode = qrCodeService.findById(id)
                .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

        model.addAttribute("qrCode", qrCode);
        return "admin/qr-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateQrCode(@PathVariable UUID id,
                              @RequestParam String description,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiryDate,
                              @RequestParam Integer maxArticles,
                              RedirectAttributes redirectAttributes) {
        try {
            QrCode qrCode = qrCodeService.findById(id)
                    .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

            qrCode.setDescription(description);
            qrCode.setExpiryDate(expiryDate);
            qrCode.setMaxArticles(maxArticles);

            qrCodeService.updateQrCode(qrCode);

            redirectAttributes.addFlashAttribute("successMessage", "QR Code aggiornato con successo!");
            return "redirect:/admin/qr/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Errore durante l'aggiornamento: " + e.getMessage());
            return "redirect:/admin/qr/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteQrCode(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            qrCodeService.deleteQrCode(id);
            redirectAttributes.addFlashAttribute("successMessage", "QR Code eliminato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Errore durante l'eliminazione: " + e.getMessage());
        }
        return "redirect:/admin/qr/list";
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable UUID id,
                                                @RequestParam(defaultValue = "300") int size) {
        try {
            QrCode qrCode = qrCodeService.findById(id)
                    .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

            byte[] qrCodeImage = qrCodeService.generateQrCodeImage(qrCode.getQrId(), size, size);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "qr-" + qrCode.getQrId() + ".png");

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}