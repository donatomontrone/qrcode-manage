package com.example.qrapp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static com.example.qrapp.constants.Constant.PNG;
import static com.example.qrapp.constants.Message.QR_CODE_GENERATION_ERROR;

@Component
public class QrCodeGenerator {

    public byte[] generateQrCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, PNG.getValue(), outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException(QR_CODE_GENERATION_ERROR.getValue(), e);
        }
    }

    public String generateQrCodeBase64(String text, int width, int height) {
        byte[] qrCodeBytes = generateQrCodeImage(text, width, height);
        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }
}