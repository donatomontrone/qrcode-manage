package com.example.qrapp.controller;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.User;
import com.example.qrapp.service.ArticleService;
import com.example.qrapp.service.QrCodeService;
import com.example.qrapp.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

  private final QrCodeService qrCodeService;

  private final UserService userService;

  private final ArticleService articleService;

  private final List<String> colors = List.of(
      "red", "blue", "green", "yellow", "purple", "orange", "teal", "pink", "brown", "rose");

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    long totalQrCodes = qrCodeService.countAll();
    long activeQrCodes = qrCodeService.countActive();
    long expiredQrCodes = qrCodeService.countExpired();
    long totalUsers = userService.countAll();
    long totalArticles = articleService.countAll();
    Pageable pageable = PageRequest.of(0, 5);
    Page<QrCode> recentQrCodes = qrCodeService.findAll(pageable, null, null);

    model.addAttribute("totalQrCodes", totalQrCodes);
    model.addAttribute("activeQrCodes", activeQrCodes);
    model.addAttribute("expiredQrCodes", expiredQrCodes);
    model.addAttribute("totalUsers", totalUsers);
    model.addAttribute("recentQrCodes", recentQrCodes);
    model.addAttribute("totalArticles", totalArticles);
    return "admin/dashboard";
  }

  @GetMapping("/qr/list")
  public String qrList(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(required = false) String filter,
                       @RequestParam(required = false) String search,
                       Model model) {
    Pageable pageable = PageRequest.of(page, size);
    Page<QrCode> qrCodesPage = qrCodeService.findAll(pageable, filter, search);
    model.addAttribute("colors", colors);
    model.addAttribute("qrCodes", qrCodesPage.getContent());
    model.addAttribute("page", pageable);
    model.addAttribute("totalPages", qrCodesPage.getTotalPages());
    model.addAttribute("lastPage", qrCodesPage.getTotalPages() > 0 ? qrCodesPage.getTotalPages() - 1 : qrCodesPage.getTotalPages());
    model.addAttribute("totalElements", qrCodesPage.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("filter", filter);
    model.addAttribute("search", search);
    return "admin/qr-list";
  }


  @GetMapping("/qr/create")
  public String qrCreateForm(Model model, Principal principal) {
    Pageable pageable = PageRequest.of(0, 10);
    List<User> recentUsers = userService.findRecentUsers();

    model.addAttribute("colors", colors);
    model.addAttribute("recentUsers", recentUsers);
    model.addAttribute("currentUser", principal.getName());
    model.addAttribute("users", userService.findAll(pageable).getContent());
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
                      @RequestParam(defaultValue = "5") int size,
                      @RequestParam(required = false) String filter,
                      @RequestParam(required = false) String search,
                      Model model, Principal principal) {

    Pageable pageable = PageRequest.of(page, size);
    Page<User> usersPage = userService.findAll(pageable, filter, search);
    Long todayRegistrations = userService.countRegistrationsToday();
    Long countAdmins = userService.countAdmins();

    model.addAttribute("colors", colors);
    model.addAttribute("users", usersPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalPages", usersPage.getTotalPages());
    model.addAttribute("totalElements", usersPage.getTotalElements());
    model.addAttribute("todayRegistrations", todayRegistrations);
    model.addAttribute("totalAdmin", countAdmins);
    model.addAttribute("filter", filter);
    model.addAttribute("search", search);
    model.addAttribute("currentUser", principal.getName());
    return "admin/users";
  }
}