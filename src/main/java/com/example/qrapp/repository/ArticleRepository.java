package com.example.qrapp.repository;

import com.example.qrapp.model.Article;
import com.example.qrapp.model.QrCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    List<Article> findByQrCodeOrderByCreatedAtDesc(QrCode qrCode);

    Page<Article> findByQrCodeOrderByCreatedAtDesc(QrCode qrCode, Pageable pageable);

    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByQrCode(QrCode qrCode);
}