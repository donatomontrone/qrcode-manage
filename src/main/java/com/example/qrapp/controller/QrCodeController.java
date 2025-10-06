package com.example.qrapp.controller;

import com.example.qrapp.dto.QrCodeDTO;
import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.service.ArticleService;
import com.example.qrapp.service.QrCodeService;
import com.example.qrapp.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/qr")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    private final UserService userService;
    private final List<String> colors = List.of(
            "red", "blue", "green", "yellow", "purple", "orange", "teal", "pink", "brown", "rose");

    @Value("${app.url}")
    private String appUrl;

    @PostMapping("/create")
    public String createQrCode(
            @Valid @ModelAttribute("qrCodeDTO") QrCodeDTO qrCodeDTO,
            BindingResult bindingResult,
            @RequestParam(required = false, defaultValue = "false") boolean darkMode,
            RedirectAttributes redirectAttributes, Model model, Principal principal) {
        if (!userService.existsByEmail(qrCodeDTO.getOwnerEmail()) && !qrCodeDTO.getOwnerEmail().isBlank()) {
            bindingResult.rejectValue("ownerEmail", "email.registered",
                    "Questa email non è presente nel database");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("colors", colors);
            model.addAttribute("recentUsers", userService.findRecentUsers());
            model.addAttribute("currentUser", principal.getName());
            model.addAttribute("qrCodeDTO", qrCodeDTO);
            model.addAttribute("users", userService.findAll());
          model.addAttribute("emails", userService.findAll().stream().map(User::getEmail).toList());
            return "admin/qr-create";
        }

        User owner = userService.findByEmail(qrCodeDTO.getOwnerEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        QrCode qrCode = qrCodeService.createQrCode(qrCodeDTO, owner);

        redirectAttributes.addFlashAttribute("url", appUrl + qrCode.getQrId());
        redirectAttributes.addFlashAttribute("owner", owner);
        redirectAttributes.addFlashAttribute("qr",
                qrCodeService.generateQrCodeBase64(qrCode.getQrId(), 192, 192, darkMode));
        return "redirect:/admin/qr/create";
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
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                               LocalDateTime expiryDate,
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
}