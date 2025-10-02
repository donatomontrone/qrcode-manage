package com.example.qrapp.controller;

import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@RequestMapping("/admin/users")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


  @GetMapping("/elimina/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public String eliminaUtente(@PathVariable UUID id, RedirectAttributes attributes) {
    User user = userService.findById(id).orElse(null);
    String message;
    String color;

    if (user != null) {
      try {
        message = "Utente '" + user.getEmail() + "' eliminato con successo!";
        color = "success";
        user.setQrCodes(null);
        user.setRoles(null);
        userService.deleteUser(id);
      } catch (Exception e) {
        message = "Errore durante l'eliminazione dell'utente: " + e.getMessage();
        color = "danger";
      }
    } else {
      message = "Utente non trovato!";
      color = "danger";
    }

    attributes.addFlashAttribute("message", message);
    attributes.addFlashAttribute("color", color);
    return "redirect:/admin/users";
  }

}
