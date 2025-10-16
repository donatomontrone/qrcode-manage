package com.example.qrapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SizeIfNotEmptyValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeIfNotEmpty {
  String message() default "La lunghezza del campo deve essere compresa tra {min} e {max} caratteri";
  Class<?>[] groups() default {};
  int min() default 0;
  int max() default Integer.MAX_VALUE;
  Class<? extends Payload>[] payload() default {};
}