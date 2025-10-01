package com.example.qrapp.service;

import com.example.qrapp.model.QrCode;
import com.example.qrapp.model.Role;
import com.example.qrapp.model.User;
import com.example.qrapp.repository.RoleRepository;
import com.example.qrapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.qrapp.constants.Constant.ADMIN;
import static com.example.qrapp.constants.Constant.USER;
import static com.example.qrapp.constants.Message.*;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + email));
    }

    public void registerUser(String firstName, String lastName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(EMAIL_ALREADY_EXISTS + email);
        }

        User user = new User(firstName, lastName, email, passwordEncoder.encode(password));

        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new RuntimeException(INIT_ROLE_USER_NOT_FOUND.getValue()));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }

    public User createAdminUser(String firstName, String lastName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(EMAIL_ALREADY_EXISTS + email);
        }

        User user = new User(firstName, lastName, email, passwordEncoder.encode(password));

        Role adminRole = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new RuntimeException(INIT_ROLE_ADMIN_NOT_FOUND.getValue()));
        user.setRoles(Set.of(adminRole));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable, String filter, String search) {
        Page<User> usersPage;

        usersPage = switch (filter != null ? filter.toLowerCase() : "") {
            case "user" -> findAllUserByRole(USER.name(), pageable);
            case "admin" -> findAllUserByRole(ADMIN.name(), pageable);
            default -> userRepository.findAll(pageable);
        };

        if (search != null && !search.trim().isEmpty()) {
            List<User> filteredList = usersPage.getContent().stream()
                    .filter(user -> user.getLastName().toLowerCase().contains(search.toLowerCase()) ||
                            user.getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredList, pageable, usersPage.getTotalElements());
        }
        return usersPage;
    }

    @Transactional
    public Page<User> findAllUserByRole(String role, Pageable pageable) {
        return userRepository.findAllUserByRole(role, pageable);
    }


    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return userRepository.count();
    }

    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> Role.ADMIN.equals(role.getName()));
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Long countRegistrationsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
        return userRepository.countRegistrationToday(startOfDay, endOfDay);
    }

    public Page<User> findAllUsersRegisteredToday(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
        return userRepository.findAllUserRegisteredToday(startOfDay, endOfDay, pageable);
    }

    public Long countAdmins() {
        return userRepository.countAdmins();
    }

    public boolean isEmailUnique(String email) {
        return userRepository.isEmailUnique(email);
    }
}