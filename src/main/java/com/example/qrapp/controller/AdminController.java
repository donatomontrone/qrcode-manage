package com.example.qrapp.controller;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.service.QrCodeService;
import com.example.qrapp.service.UserService;
import com.example.qrapp.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final QrCodeService qrCodeService;

    private final UserService userService;

    private final ArticleService articleService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        long totalQrCodes = qrCodeService.countAll();
        long activeQrCodes = qrCodeService.countActive();
        long expiredQrCodes = qrCodeService.countExpired();
        long totalUsers = userService.countAll();
        long totalArticles = articleService.countAll();

        // QR code recenti per visualizzazione
        Pageable pageable = PageRequest.of(0, 5);
        Page<QrCode> recentQrCodes = qrCodeService.findAll(pageable, null, null);

        model.addAttribute("totalQrCodes", totalQrCodes);
        model.addAttribute("activeQrCodes", activeQrCodes);
        model.addAttribute("expiredQrCodes", expiredQrCodes);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalArticles", totalArticles);
        model.addAttribute("recentQrCodes", recentQrCodes);

        return "admin/dashboard";
    }

    @GetMapping("/qr/list")
    public String qrList(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size,
                         @RequestParam(required = false) String filter,
                         @RequestParam(required = false) String search,
                         Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<QrCode> qrCodesPage = qrCodeService.findAll(pageable, filter, search);

        model.addAttribute("qrCodes", qrCodesPage.getContent());
        model.addAttribute("page", pageable);
        model.addAttribute("totalPages", qrCodesPage.getTotalPages());
        model.addAttribute("lastPage", qrCodesPage.getTotalPages() - 1);
        model.addAttribute("totalElements", qrCodesPage.getTotalElements());
        model.addAttribute("filter", filter);
        model.addAttribute("search", search);
        System.out.println(pageable);
        System.out.println(qrCodesPage.getTotalPages());
        System.out.println(qrCodesPage.getTotalElements());
        System.out.println(qrCodesPage.getTotalPages() - 1);
        return "admin/qr-list";
    }



    @GetMapping("/qr/create")
    public String qrCreateForm(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> recentUsers = userService.findAll(pageable).getContent();

        model.addAttribute("recentUsers", recentUsers);
        return "admin/qr-create";
    }

    @GetMapping("/qr/{id}")
    public String qrDetail(@PathVariable UUID id, Model model) {
        QrCode qrCode = qrCodeService.findById(id)
                .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

        model.addAttribute("qrCode", qrCode);
        return "admin/qr-detail";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userService.findAll(pageable);
        Long todayRegistrations = userService.countRegistrationsToday();
        Long countAdmins = userService.countAdmins();

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("todayRegistrations", todayRegistrations);
        model.addAttribute("totalAdmin", countAdmins);

        return "admin/users";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        // Report e statistiche avanzate
        model.addAttribute("totalQrCodes", qrCodeService.countAll());
        model.addAttribute("activeQrCodes", qrCodeService.countActive());
        model.addAttribute("expiredQrCodes", qrCodeService.countExpired());

        return "admin/reports";
    }
}