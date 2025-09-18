package com.example.qrapp.service;

import com.example.qrapp.exception.QrCodeExpiredException;
import com.example.qrapp.model.Article;
import com.example.qrapp.model.QrCode;
import com.example.qrapp.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.qrapp.constants.Message.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final CloudinaryService cloudinaryService;

    public Article createArticle(String name, String description, MultipartFile imageFile, QrCode qrCode) {
        if (!qrCode.canAddArticle()) {
            if (qrCode.isExpired()) {
                throw new QrCodeExpiredException(QR_CODE_EXPIRED.getValue());
            }
            throw new IllegalStateException(QR_CODE_FULL.getValue());
        }

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(imageFile);
        }
        Article article = new Article(name, description, imageUrl, qrCode);
        return articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public Optional<Article> findById(UUID id) {
        return articleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Article> findByQrCode(QrCode qrCode) {
        return articleRepository.findByQrCodeOrderByCreatedAtDesc(qrCode);
    }

    @Transactional(readOnly = true)
    public Page<Article> findByQrCode(QrCode qrCode, Pageable pageable) {
        return articleRepository.findByQrCodeOrderByCreatedAtDesc(qrCode, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Article updateArticle(Article article, String name, String description, MultipartFile imageFile) {
        if (!article.getQrCode().canAddArticle() && !article.getQrCode().isExpired()) {
            throw new QrCodeExpiredException(QR_CODE_EXPIRED_EDIT.getValue());
        }
        boolean isDeleted = false;
        article.setName(name);
        article.setDescription(description);
        if (imageFile != null && !imageFile.isEmpty()) {
            if (article.getImageUrl() != null) {
                isDeleted = cloudinaryService.deleteImage(article.getImageUrl());
            }
            if (isDeleted) {
                String newImageUrl = cloudinaryService.uploadImage(imageFile);
                article.setImageUrl(newImageUrl);
            } else {
                throw new IllegalStateException(ERROR_DELETE_IMAGE.getValue());
            }
        }
        return articleRepository.save(article);
    }

    public void deleteArticle(UUID id) {
        Optional<Article> article = articleRepository.findById(id);
        boolean isDeleted = false;
        if (article.isPresent()) {
            if (article.get().getImageUrl() != null) {
                isDeleted = cloudinaryService.deleteImage(article.get().getImageUrl());
            }
            if (!isDeleted) {
                articleRepository.deleteById(id);
                throw new IllegalStateException(ERROR_DELETE_IMAGE_ARTICLE.getValue());
            }
            articleRepository.deleteById(id);
        }
    }

    public void deleteArticle(Article article) {
        boolean isDeleted = false;
        if (article.getImageUrl() != null) {
            isDeleted = cloudinaryService.deleteImage(article.getImageUrl());
        }
        if (!isDeleted) {
            articleRepository.delete(article);
            throw new IllegalStateException(ERROR_DELETE_IMAGE_ARTICLE.getValue());
        }
        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByQrCode(QrCode qrCode) {
        return articleRepository.countByQrCode(qrCode);
    }
}