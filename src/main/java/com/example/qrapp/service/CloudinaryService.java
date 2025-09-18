package com.example.qrapp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.example.qrapp.constants.Constant.*;
import static com.example.qrapp.constants.Message.ERROR_DELETE_IMAGE;
import static com.example.qrapp.constants.Message.ERROR_UPLOAD_IMAGE;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private static final String OK = "ok";
    private static final String RESULT = "result";
    private static final String UPLOAD = "/upload/";

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    FOLDER.getValue(), QR_APP_ARTICLES.getValue(),
                    RESOURCE_TYPE.getValue(), IMAGE.getValue(),
                    FORMAT.getValue(), JPG.getValue(),
                    QUALITY.getValue(), AUTO_GOOD.getValue(),
                    FETCH_FORMAT.getValue(), AUTO.getValue()
                )
            );
            return (String) uploadResult.get(SECURE_URL.getValue());
        } catch (IOException e) {
            throw new RuntimeException(ERROR_UPLOAD_IMAGE.getValue(), e);
        }
    }

    public boolean deleteImage(String imageUrl) {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                return OK.equals(result.get(RESULT));
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(ERROR_DELETE_IMAGE.getValue(), e);
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        int uploadIndex = imageUrl.indexOf(UPLOAD);
        if (uploadIndex == -1) {
            return null;
        }

        String afterUpload = imageUrl.substring(uploadIndex + 8);

        int versionEnd = afterUpload.indexOf('/');
        if (versionEnd != -1 && afterUpload.substring(0, versionEnd).matches("v\\d+")) {
            afterUpload = afterUpload.substring(versionEnd + 1);
        }
        int dotIndex = afterUpload.lastIndexOf('.');
        if (dotIndex != -1) {
            afterUpload = afterUpload.substring(0, dotIndex);
        }
        return afterUpload;
    }
}