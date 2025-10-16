package com.example.qrapp.validator;

import jakarta.validation.ConstraintValidator;

public class SizeIfNotEmptyValidator implements ConstraintValidator<SizeIfNotEmpty, String> {

  private int min;
  private int max;

  @Override
  public void initialize(SizeIfNotEmpty constraintAnnotation) {
    this.min = constraintAnnotation.min();
    this.max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }
    int length = value.length();
    return length >= min && length <= max;
  }
}
