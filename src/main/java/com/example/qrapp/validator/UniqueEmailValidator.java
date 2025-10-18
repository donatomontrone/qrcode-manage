package com.example.qrapp.validator;

import com.example.qrapp.dto.UserEditDTO;
import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, UserEditDTO> {

    private final UserService userService;

    @Override
    public boolean isValid(UserEditDTO dto, ConstraintValidatorContext context) {
        if (dto.getEmail() == null) return true; // il @NotBlank lo gestisce altrove
        Optional<User> existing = userService.findByEmail(dto.getEmail());
        return existing.map(user -> user.getId().equals(dto.getId())).orElse(true);
    }
}
