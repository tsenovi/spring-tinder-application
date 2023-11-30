package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.constraints.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public interface PasswordValidator extends ConstraintValidator<Password, String> {
    @Override
    boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext);
}
