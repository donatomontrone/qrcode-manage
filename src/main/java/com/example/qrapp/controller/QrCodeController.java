package com.example.qrapp.controller;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.service.QrCodeService;
import com.example.qrapp.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/qr")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class QrCodeController {

  private final QrCodeService qrCodeService;

  private final UserService userService;

  @Value("${app.url}")
  private  String appUrl;

  @PostMapping("/create")
  public String createQrCode(@RequestParam String ownerEmail,
                             @RequestParam String description,
                             @RequestParam String expiryDate,
                             @RequestParam Integer maxArticles,
                             @RequestParam(required = false, defaultValue = "false") boolean darkMode,
                             RedirectAttributes redirectAttributes) {
    try {
      User owner = userService.findByEmail(ownerEmail)
          .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + ownerEmail));

      LocalDate date = LocalDate.parse(expiryDate, DateTimeFormatter.ISO_DATE);
      LocalDateTime expiryDateTime = date.atStartOfDay();

      QrCode qrCode = qrCodeService.createQrCode(description, owner, expiryDateTime, maxArticles);

      redirectAttributes.addFlashAttribute("successMessage",
          "QR Code creato con successo! ID: " + qrCode.getQrId());
      redirectAttributes.addFlashAttribute("url",
           appUrl + qrCode.getQrId());
      redirectAttributes.addFlashAttribute("qr",
          qrCodeService.generateQrCodeBase64(qrCode.getQrId(), 192, 192, darkMode));
      return "redirect:/admin/qr/create";

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