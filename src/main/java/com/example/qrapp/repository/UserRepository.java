package com.example.qrapp.repository;

import com.example.qrapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u  WHERE u.createdAt BETWEEN :startOfDay AND :endOfDay")
    Long countRegistrationToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startOfDay AND :endOfDay")
    Page<User> findAllUserRegisteredToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'ADMIN'")
    Long countAdmins();

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean isEmailUnique(String email);
}