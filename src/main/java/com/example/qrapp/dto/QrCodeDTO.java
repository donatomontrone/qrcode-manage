package com.example.qrapp.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.qrapp.constants.Constant.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeDTO {

    private String qrId;

    @Size(max = 255, message = "La descrizione se presente non deve essere superiore ai 255 caratteri")
    private String description;

    @NotBlank(message = "Inserisci la mail dell''utente per associare il QR code")
    private String ownerEmail;

    @Future(message = "La data di scadenza del QR code deve essere successiva a oggi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Positive(message = "Il numero di articoli totali deve essere maggiore di ZERO")
    private Integer maxArticles;
}