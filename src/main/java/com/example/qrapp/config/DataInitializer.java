package com.example.qrapp.config;

import com.example.qrapp.model.Role;
import com.example.qrapp.model.User;
import com.example.qrapp.repository.RoleRepository;
import com.example.qrapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
            initializeAdminUser();
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

    private void initializeAdminUser() {
        logger.info(INIT_ADMIN_USER.getValue());

        // Controlla se esiste già un utente admin
        if (!userRepository.existsByEmail(adminEmail)) {
            // Recupera il ruolo ADMIN
            Role adminRole = roleRepository.findByName(Role.ADMIN)
                    .orElseThrow(() -> new RuntimeException(INIT_ROLE_ADMIN_NOT_FOUND.getValue()));

            // Crea l'utente admin
            User adminUser = new User();
            adminUser.setFirstName(ADMIN.getValue());
            adminUser.setLastName(USER.getValue());
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRoles(Set.of(adminRole));

            userRepository.save(adminUser);

            logger.info("Utente amministratore creato con email: {}", adminEmail);
            logger.info("Password: {} (cambiarla dopo il primo accesso!)", adminPassword);
        } else {
            logger.info("Utente amministratore già esistente con email: {}", adminEmail);
        }
    }
}