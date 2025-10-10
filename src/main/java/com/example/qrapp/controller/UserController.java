package com.example.qrapp.controller;

import com.example.qrapp.dto.UserEditDTO;
import com.example.qrapp.mapper.InstanceMapper;
import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@RequestMapping("/admin/users")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final InstanceMapper instanceMapper;


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


  @GetMapping("/{id}")
  public String viewUser(@PathVariable UUID id, Model model) {
    User user = userService.findById(id)
        .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + id));
    model.addAttribute("user", instanceMapper.userToUserEditDTO(user));
    return "admin/edit-user";
  }

  @PostMapping("/{id}")
  public String updateUser(@PathVariable UUID id, @Valid @ModelAttribute UserEditDTO user,
                           BindingResult bindingResult,
                           RedirectAttributes attributes) {
    Optional<User> userOpt = userService.findById(id);

    if (userOpt.isPresent()) {
      User currentUser = userOpt.get();
      if (userService.existsEmail(user.getEmail())) {
        bindingResult.rejectValue("email", "error.user", "Email già utilizzata.");
      }

      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
          bindingResult.rejectValue("password", "error.user", "Le password non coincidono.");
          bindingResult.rejectValue("confirmPassword", "error.user", "Le password non coincidono.");
        }
      }

      if (bindingResult.hasErrors()) {
        return "admin/edit-user";
      }

      currentUser.setFirstName(user.getFirstName() != null ? user.getFirstName() : currentUser.getFirstName());
      currentUser.setLastName(user.getLastName()  != null ? user.getLastName() : currentUser.getLastName());
      currentUser.setEmail(user.getEmail()  != null ? user.getEmail() : currentUser.getEmail());

      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
      }

      userService.updateUser(currentUser);
      attributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo.");
    }
    attributes.addFlashAttribute("errorMessage", "Profilo non aggiornato.");
    return "redirect:/admin/users";
  }
}
