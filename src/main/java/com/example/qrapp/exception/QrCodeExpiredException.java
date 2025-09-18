package com.example.qrapp.exception;

public class QrCodeExpiredException extends RuntimeException {

    public QrCodeExpiredException(String message) {
        super(message);
    }

    public QrCodeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}