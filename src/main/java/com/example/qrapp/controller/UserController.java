package com.example.qrapp.controller;

import com.example.qrapp.dto.UserEditDTO;
import com.example.qrapp.mapper.InstanceMapper;
import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import com.example.qrapp.validator.UniqueEmailValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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

    private final UniqueEmailValidator uniqueEmailValidator;

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
                           BindingResult bindingResult, Model model,
                           RedirectAttributes attributes, HttpServletRequest request, HttpServletResponse response) {
    Optional<User> userOpt = userService.findById(id);
    if (userOpt.isPresent()) {
      User currentUser = userOpt.get();
      uniqueEmailValidator.setCurrentUserId(currentUser.getId());
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
          bindingResult.rejectValue("password", "error.password", "Le password non coincidono");
          bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Le password non coincidono");
        }
      }
      if (bindingResult.hasErrors()) {
        model.addAttribute("errors", bindingResult);
        model.addAttribute("user", user);
        return "admin/edit-user";
      }
      boolean emailChanged = !currentUser.getEmail().equals(user.getEmail());
      userService.updateUser(user, currentUser);
      if (emailChanged) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        attributes.addFlashAttribute("logoutMessage",
            "Email modificata con successo. Effettua nuovamente il login.");
        return "redirect:/login";
      }
      attributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo.");
    }
    attributes.addFlashAttribute("errorMessage", "Profilo non aggiornato.");
    return "redirect:/admin/users";
  }
}
