package com.example.qrapp.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.example.qrapp.constants.Constant.*;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put(CLOUD_NAME.getValue(), cloudName);
        config.put(API_KEY.getValue(), apiKey);
        config.put(API_SECRET.getValue(), apiSecret);
        config.put(SECURE.getValue(), TRUE.getValue());

        return new Cloudinary(config);
    }
}