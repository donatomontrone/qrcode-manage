package com.example.qrapp.controller;

import com.example.qrapp.dto.UserEditDTO;
import com.example.qrapp.mapper.InstanceMapper;
import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import com.example.qrapp.validator.UniqueEmailValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final UserService userService;
  private final UniqueEmailValidator uniqueEmailValidator;
  private final InstanceMapper instanceMapper;

  @GetMapping
  public String viewProfile(Principal principal, Model model) {
    User owner = userService.findByEmail(principal.getName())
        .orElseThrow(
            () -> new RuntimeException("Utente non trovato con email: " + principal.getName()));
    model.addAttribute("user", instanceMapper.userToUserEditDTO(owner));
    return "public/edit-profile";
  }

  @PostMapping
  public String updateProfile(@ModelAttribute("user") UserEditDTO user, Model model,
                              BindingResult bindingResult, Principal principal,
                              RedirectAttributes attributes, HttpServletRequest request,
                              HttpServletResponse response) {
    Optional<User> userOpt = userService.findByEmail(principal.getName());
    uniqueEmailValidator.setCurrentUserId(user.getId());
    if (userOpt.isPresent()) {
      User currentUser = userOpt.get();
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
          bindingResult.rejectValue("password", "error.password", "Le password non coincidono");
          bindingResult.rejectValue("confirmPassword", "error.confirmPassword",
              "Le password non coincidono");
        }
      }
      if (bindingResult.hasErrors()) {
        model.addAttribute("errors", bindingResult);
        model.addAttribute("user", user);
        return "public/edit-profile";
      }
      if (userService.userCanUpdate(user, currentUser)) {
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
    } else {
      attributes.addFlashAttribute("errorMessage", "Profilo non aggiornato.");
    }
    return "redirect:/profile";
  }
}
