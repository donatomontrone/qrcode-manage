package com.example.qrapp.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Message {

    //Data Intialization
    INIT_APP("Inizializzazione dati dell'applicazione..."),
    INIT_COMPLETED("Inizializzazione completata con successo!"),
    INIT_ERROR("Errore durante l'inizializzazione dei dati"),
    INIT_ROLES("Inizializzazione ruoli..."),
    INIT_ROLE_ADMIN("Amministratore del sistema"),
    INIT_ROLE_ADMIN_CREATED("Ruolo ADMIN creato."),
    INIT_ROLE_ADMIN_EXISTS("Ruolo ADMIN già esistente."),
    INIT_ROLE_ADMIN_NOT_FOUND("Ruolo ADMIN non trovato!"),
    INIT_ROLE_USER("Utente standard"),
    INIT_ROLE_USER_CREATED("Ruolo USER creato."),
    INIT_ROLE_USER_EXISTS("Ruolo USER già esistente."),
    INIT_ROLE_USER_NOT_FOUND("Ruolo USER non trovato!"),
    INIT_ADMIN_USER("Inizializzazione utente amministratore..."),
    EMAIL_ALREADY_EXISTS("Email già registrata: "),
    USER_NOT_FOUND("Utente non trovato con email: "),

    //Cloudinary
    ERROR_UPLOAD_IMAGE("Errore durante il caricamento dell'immagine."),
    ERROR_DELETE_IMAGE("Errore durante l'eliminazione dell'immagine."),
    ERROR_DELETE_IMAGE_ARTICLE("Articolo è stato eliminato ma si è verificato un errore durante l'eliminazione dell'immagine associata."),

    //Exception
    GENERIC_ERROR("Si è verificato un errore. Contatta l'amministratore se persiste."),
    RUNTIME_ERROR("Si è verificato un errore imprevisto. Riprova più tardi."),

    //QR Code
    QR_CODE_EXPIRED("Il QR code è scaduto e non è possibile aggiungere articoli, contatta l'amministratore per rinnovare il QR Code"),
    QR_CODE_FULL("Il QR code ha raggiunto il numero massimo di articoli disponibili, contatta l'amministratore per fartene aggiungere altri"),
    QR_CODE_EXPIRED_EDIT("Il QR code è scaduto e non è possibile modificare gli articoli, contatta l'amministratore per rinnovare il QR Code"),
    QR_CODE_GENERATION_ERROR("Errore durante la generazione del QR code."),
    ;
    private final String value;

}
