package com.example.qrapp.validator;

import com.example.qrapp.model.User;
import com.example.qrapp.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;

@Component
@RequestScope
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

  private final UserService userService;

  @Setter
  private UUID currentUserId;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null || email.isBlank()) return true;

    User existingUser = userService.findByEmail(email).orElse(null);
    if (existingUser == null) return true;

    return existingUser.getId().equals(currentUserId);
  }
}
