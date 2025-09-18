package com.example.qrapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.example.qrapp.constants.Constant.*;
import static com.example.qrapp.constants.Message.GENERIC_ERROR;
import static com.example.qrapp.constants.Message.RUNTIME_ERROR;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(QrCodeExpiredException.class)
    public String handleQrCodeExpired(QrCodeExpiredException ex, Model model) {
        logger.warn("QR Code scaduto: {}", ex.getMessage());
        model.addAttribute(ERROR_MESSAGE.getValue(), ex.getMessage());
        return QR_EXPIRED.getValue();
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, Model model, RedirectAttributes redirectAttributes) {
        logger.warn("Stato non valido: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE.getValue(), ex.getMessage());
        return REDIRECT.getValue() + ERROR.getValue();
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.error("Errore runtime: {}", ex.getMessage(), ex);
        model.addAttribute(ERROR_MESSAGE.getValue(), RUNTIME_ERROR.getValue());
        return ERROR.getValue();
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Errore generico: {}", ex.getMessage(), ex);
        model.addAttribute(ERROR_MESSAGE.getValue(), GENERIC_ERROR.getValue());
        return ERROR.getValue();
    }
}