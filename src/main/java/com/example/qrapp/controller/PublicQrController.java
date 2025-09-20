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
}