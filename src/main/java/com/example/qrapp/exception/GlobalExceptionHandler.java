package com.example.qrapp.exception;

import static com.example.qrapp.constants.Constant.ERROR;
import static com.example.qrapp.constants.Constant.ERROR_MESSAGE;
import static com.example.qrapp.constants.Constant.STATUS;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(QrCodeExpiredException.class)
  public String handleQrCodeExpired(QrCodeExpiredException ex, Model model) {
    logger.warn("QR Code scaduto: {}", ex.getMessage());
    model.addAttribute(ERROR_MESSAGE.getValue(), ex.getMessage());
    model.addAttribute(STATUS.getValue(), HttpStatus.BAD_REQUEST.value());
    return "redirect:/" + ERROR.getValue();
  }

  @ExceptionHandler(IllegalStateException.class)
  public String handleIllegalState(IllegalStateException ex, Model model) {
    logger.warn("Stato non valido: {}", ex.getMessage());
    model.addAttribute(ERROR_MESSAGE.getValue(), ex.getMessage());
    model.addAttribute(STATUS.getValue(), HttpStatus.BAD_REQUEST.value());
    return "redirect:/" + ERROR.getValue();
  }

  @ExceptionHandler(RuntimeException.class)
  public String handleRuntimeException(RuntimeException ex, Model model) {
    logger.error("Errore runtime: {}", ex.getMessage(), ex);
    model.addAttribute(ERROR_MESSAGE.getValue(), ex.getMessage());
    model.addAttribute(STATUS.getValue(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    return "redirect:/" + ERROR.getValue();
  }

  @ExceptionHandler(Exception.class)
  public String handleGenericException(Exception ex, Model model) {
    logger.error("Errore generico: {}", ex.getMessage(), ex);
    model.addAttribute(ERROR_MESSAGE.getValue(), ex.getMessage());
    model.addAttribute(STATUS.getValue(), HttpStatus.NOT_IMPLEMENTED.value());
    return "redirect:/" + ERROR.getValue();
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public String handleGenericException(NoResourceFoundException ex, Model model, HttpServletRequest request) {
    logger.error("Errore: {}", ex.getMessage(), ex);
    model.addAttribute(ERROR_MESSAGE.getValue(), "Indirizzo " + request.getRequestURI() + " non trovato!");
    model.addAttribute(STATUS.getValue(),HttpStatus.BAD_REQUEST.value());
    return ERROR.getValue();
  }
}