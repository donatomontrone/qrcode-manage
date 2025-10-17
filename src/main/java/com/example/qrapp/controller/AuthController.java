package com.example.qrapp.controller;

import com.example.qrapp.dto.UserCreateDTO;
import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import jakarta.validation.Valid;

import java.security.Principal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(principal.getName()).orElse(null);
        System.out.println(principal);
        if (user == null) {
            return "redirect:/login";
        } else {
            if (userService.isAdmin(user)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (error != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Credenziali non valide. Riprova.");
        }

        if (logout != null) {
            model.addAttribute("logout", true);
            model.addAttribute("successMessage", "Logout effettuato con successo.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerFormUser(Model model, Principal principal) {

        model.addAttribute("user", new UserCreateDTO());
        if (principal == null) {
            return "auth/register";
        }
        return "auth/register-admin";
    }

    @PostMapping("/register-user")
    public String registerUser(@Valid @ModelAttribute UserCreateDTO user,
                               BindingResult bindingResult, Model model,
                               RedirectAttributes redirectAttributes) {

        if (user.getPassword() != null && !user.getPassword().isEmpty() && user.getConfirmPassword() != null && !user.getConfirmPassword().isEmpty()) {
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                bindingResult.rejectValue("password", "password.mismatch",
                        "Le password non coincidono");
                bindingResult.rejectValue("confirmPassword", "password.mismatch",
                        "Le password non coincidono");
            }
        }

        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "Questa email è già registrata");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            model.addAttribute("user", user);
            return "auth/register";
        }

        userService.registerUser(user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPassword());
        redirectAttributes.addFlashAttribute("successMessage",
                "Registrazione completata! Puoi ora effettuare il login.");
        return "redirect:/login";
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@ModelAttribute UserCreateDTO user,
                                BindingResult bindingResult, Model model,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {

        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "Questa email è già registrata");
        }

        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.password", "Le password non coincidono");
            bindingResult.rejectValue("confirmPassword", "error.password", "Le password coincidono");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            model.addAttribute("user", user);
            return "auth/register";
        }

        User newUser = userService.createAdminUser(user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPassword());
        redirectAttributes.addFlashAttribute("successMessage",
                "Registrazione completata!" + newUser.getEmail() + " può ora effettuare il login.");
        return "redirect:/login";
    }
}