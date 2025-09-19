package com.example.qrapp.controller;

import com.example.qrapp.exception.QrCodeExpiredException;
import com.example.qrapp.model.Article;
import com.example.qrapp.model.QrCode;
import com.example.qrapp.service.ArticleService;
import com.example.qrapp.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/qr")
@RequiredArgsConstructor
public class PublicQrController {

    private final QrCodeService qrCodeService;

    private final ArticleService articleService;

    @GetMapping("/{qrId}")
    public String viewQrCode(@PathVariable String qrId, Model model) {
        try {
            QrCode qrCode = qrCodeService.findByQrId(qrId)
                    .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

            if (qrCode.isExpired()) {
                model.addAttribute("qrCode", qrCode);
                return "public/qr-expired";
            }

            List<Article> articles = articleService.findByQrCode(qrCode);

            model.addAttribute("qrCode", qrCode);
            model.addAttribute("articles", articles);

            return "public/qr-articles";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "QR Code non valido o non trovato");
            return "public/qr-expired";
        }
    }

    @PostMapping("/{qrId}/articles")
    public String addArticle(@PathVariable String qrId,
                            @RequestParam String name,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) MultipartFile image,
                            RedirectAttributes redirectAttributes) {
        try {
            QrCode qrCode = qrCodeService.findByQrId(qrId)
                    .orElseThrow(() -> new RuntimeException("QR Code non trovato"));

            Article article = articleService.createArticle(name, description, image, qrCode);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Articolo '" + article.getName() + "' aggiunto con successo!");

        } catch (QrCodeExpiredException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Errore durante l'aggiunta dell'articolo: " + e.getMessage());
        }

        return "redirect:/qr/" + qrId;
    }

    @PostMapping("/{qrId}/articles/{articleId}/edit")
    public String editArticle(@PathVariable String qrId,
                             @PathVariable UUID articleId,
                             @RequestParam String name,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        try {
            Article article = articleService.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Articolo non trovato"));

            articleService.updateArticle(article, name, description, image);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Articolo aggiornato con successo!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Errore durante l'aggiornamento: " + e.getMessage());
        }

        return "redirect:/qr/" + qrId;
    }

    @PostMapping("/{qrId}/articles/{articleId}/delete")
    public String deleteArticle(@PathVariable String qrId,
                                @PathVariable UUID articleId,
                                RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(articleId);
            redirectAttributes.addFlashAttribute("successMessage", "Articolo eliminato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Errore durante l'eliminazione: " + e.getMessage());
        }

        return "redirect:/qr/" + qrId;
    }
}