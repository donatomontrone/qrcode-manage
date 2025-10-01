package com.example.qrapp.config;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.Role;
import com.example.qrapp.model.User;
import com.example.qrapp.repository.QrCodeRepository;
import com.example.qrapp.repository.RoleRepository;
import com.example.qrapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.example.qrapp.constants.Constant.ADMIN;
import static com.example.qrapp.constants.Constant.USER;
import static com.example.qrapp.constants.Message.*;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final QrCodeRepository qrCodeRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@qrmanager.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        logger.info(INIT_APP.getValue());

        try {
            initializeRoles();
            User user = initializeAdminUser();
            initializeQRCodes(user);
            logger.info(INIT_COMPLETED.getValue());
        } catch (Exception e) {
            logger.error(INIT_ERROR.getValue(), e);
            throw e;
        }
    }

    private void initializeRoles() {
        logger.info(INIT_ROLES.getValue());

        // Crea ruolo ADMIN se non esiste
        if (!roleRepository.existsByName(Role.ADMIN)) {
            Role adminRole = new Role(Role.ADMIN, INIT_ROLE_ADMIN.getValue());
            roleRepository.save(adminRole);
            logger.info(INIT_ROLE_ADMIN_CREATED.getValue());
        } else {
            logger.info(INIT_ROLE_ADMIN_EXISTS.getValue());
        }

        // Crea ruolo USER se non esiste
        if (!roleRepository.existsByName(Role.USER)) {
            Role userRole = new Role(Role.USER, INIT_ROLE_USER.getValue());
            roleRepository.save(userRole);
            logger.info(INIT_ROLE_USER_CREATED.getValue());
        } else {
            logger.info(INIT_ROLE_USER_EXISTS.getValue());
        }
    }

    private User initializeAdminUser() {
        logger.info(INIT_ADMIN_USER.getValue());

        // Controlla se esiste già un utente admin
        if (!userRepository.existsByEmail(adminEmail)) {
            // Recupera il ruolo ADMIN
            Role adminRole = roleRepository.findByName(Role.ADMIN)
                    .orElseThrow(() -> new RuntimeException(INIT_ROLE_ADMIN_NOT_FOUND.getValue()));

            // Crea l'utente admin
            User adminUser = new User();
            adminUser.setFirstName("Donato");
            adminUser.setLastName("Montrone");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRoles(Set.of(adminRole));


            logger.info("Utente amministratore creato con email: {}", adminEmail);
            logger.info("Password: {} (cambiarla dopo il primo accesso!)", adminPassword);
            return userRepository.save(adminUser);
        } else {
            logger.info("Utente amministratore già esistente con email: {}", adminEmail);
            return null;
        }
    }

    private void initializeQRCodes(User user) {
        List<QrCode> existingQRCodes = new ArrayList<>();
        logger.info("Inizio caricamento QR per l'amministratore: {}", adminEmail);
        if (qrCodeRepository.countAll() == 0) {
            for (int i = 0; i < 5; i++) {
                QrCode qrCode = new QrCode();
                qrCode.setQrId("QR" + (i + 1));
                qrCode.setMaxArticles(new Random().nextInt(5, 11));
                qrCode.setDescription("Test " + (i + 1));
                qrCode.setExpiryDate(LocalDateTime.now().plusYears(1));
                qrCode.setOwner(user);
                existingQRCodes.add(qrCode);
            }
            logger.info("Caricamento di {} QR per l'amministratore di sistema", existingQRCodes.size());
            qrCodeRepository.saveAll(existingQRCodes);
            return;
        }
        logger.info("QR già presenti per l'amministratore: {}", adminEmail);
    }
}