package com.example.qrapp.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
  String message() default "Email già registrata";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}