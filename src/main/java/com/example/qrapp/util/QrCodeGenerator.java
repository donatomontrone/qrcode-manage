package com.example.qrapp.util;

import static com.example.qrapp.constants.Constant.PNG;
import static com.example.qrapp.constants.Message.QR_CODE_GENERATION_ERROR;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class QrCodeGenerator {

  public byte[] generateQrCodeImage(String text, int width, int height, boolean darkMode) {
    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
      int qrColor = 0xFF9CA3AF;
      int bgColor = !darkMode ? 0xFF1F2937 : 0xFFF3F4F6;

      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.MARGIN, 1);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MatrixToImageConfig config = new MatrixToImageConfig(qrColor, bgColor);
      MatrixToImageWriter.writeToStream(bitMatrix, PNG.getValue(), outputStream, config);
      return outputStream.toByteArray();
    } catch (WriterException | IOException e) {
      throw new RuntimeException(QR_CODE_GENERATION_ERROR.getValue(), e);
    }
  }

  public String generateQrCodeBase64(String text, int width, int height, boolean darkMode) {
    byte[] qrCodeBytes = generateQrCodeImage(text, width, height, darkMode);
    return Base64.getEncoder().encodeToString(qrCodeBytes);
  }
}