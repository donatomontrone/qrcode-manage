package com.example.qrapp.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Constant {
    //Cloudinary
    CLOUD_NAME("cloud_name"),
    API_KEY("api_key"),
    API_SECRET("api_secret"),
    SECURE("secure"),
    SECURE_URL("secure_url"),
    FOLDER("folder"),
    QR_APP_ARTICLES("qr-app/articles"),
    RESOURCE_TYPE("resource_type"),
    IMAGE("image"),
    FORMAT("format"),
    JPG("jpg"),
    PNG("PNG"),
    QUALITY("quality"),
    AUTO_GOOD("auto:good"),
    FETCH_FORMAT("fetch_format"),
    AUTO("auto"),
    TRUE("true"),
    ADMIN("Admin"),
    USER("User"),

    //General
    ROLE_("ROLE_"),

    //Exception
    ERROR_MESSAGE("errorMessage"),


    //Template
    ERROR("error"),
    QR_EXPIRED("public/qr-expired"),
    REDIRECT("redirect:/"),
    LOGIN("login"),


    //QR Code Status
    ACTIVE("Active"),
    EXPIRED("Expired"),
    FULL("Full")
    ;

    private final String value;
}
