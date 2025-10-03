package com.example.qrapp.controller;

import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


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
            model.addAttribute("logoutMessage", "Logout effettuato con successo.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerFormUser(Model model, Principal principal) {

        model.addAttribute("user", new User());
        if (principal == null) {
            return "auth/register";
        }
        return "auth/register-admin";
    }

    @PostMapping("/register-user")
    public String registerUser(@ModelAttribute User user,
                          BindingResult result,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.password", "Le password non coincidono");
            return "auth/register";
        }
        try {
            userService.registerUser(user.getFirstName(), user.getLastName(), 
                                   user.getEmail(), user.getPassword());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registrazione completata! Puoi ora effettuare il login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@ModelAttribute User user,
                           BindingResult result,
                           @RequestParam String confirmPassword,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.password", "Le password non coincidono");
            return "redirect:/register";
        }
        try {
            User newUser = userService.createAdminUser(user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getPassword());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registrazione completata!" + newUser.getEmail() + " può ora effettuare il login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        }
    }
}