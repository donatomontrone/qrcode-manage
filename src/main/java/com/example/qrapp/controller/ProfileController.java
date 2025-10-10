package com.example.qrapp.controller;

import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;


    @GetMapping
    public String viewProfile(Principal principal, Model model) {
        User owner = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + principal.getName()));
        model.addAttribute("userForm", owner);
        return "public/edit-profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute("userForm") User userForm,
                                BindingResult bindingResult,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Principal principal,
                                RedirectAttributes redirectAttrs) {

        Optional<User> user = userService.findByEmail(principal.getName());

        if (user.isPresent()) {
            User currentUser = user.get();
            if (userService.existsEmail(userForm.getEmail())) {
                bindingResult.rejectValue("email", "error.user", "Email già utilizzata.");
            }

            if (newPassword != null && !newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    bindingResult.rejectValue("password", "error.user", "Le password non coincidono.");
                }
            }

            if (bindingResult.hasErrors()) {
                return "public/edit-profile";
            }

            currentUser.setFirstName(userForm.getFirstName() != null ? userForm.getFirstName() : currentUser.getFirstName());
            currentUser.setLastName(userForm.getLastName()  != null ? userForm.getLastName() : currentUser.getLastName());
            currentUser.setEmail(userForm.getEmail()  != null ? userForm.getEmail() : currentUser.getEmail());

            if (newPassword != null && !newPassword.isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            }

            User updated = userService.updateUser(currentUser);
            redirectAttrs.addFlashAttribute("successMessage", "Profilo di " + updated.getEmail() + "aggiornato con successo.");
        }
        redirectAttrs.addFlashAttribute("errorMessage", "Profilo non aggiornato.");
        return "redirect:/profile";
    }
}
