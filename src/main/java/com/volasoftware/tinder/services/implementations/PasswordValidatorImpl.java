package com.volasoftware.tinder.services.implementations;

import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

import org.passay.*;
import org.springframework.stereotype.Service;

@Service
public class PasswordValidatorImpl implements com.volasoftware.tinder.services.contracts.PasswordValidator {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        org.passay.PasswordValidator passwordValidator = new org.passay.PasswordValidator(
                Arrays.asList(
                        new LengthRule(8, 128),
                        new CharacterRule(EnglishCharacterData.Digit, 1),
                        new WhitespaceRule()));

        RuleResult result = passwordValidator.validate(new PasswordData(password));

        if (result.isValid()) {

            return true;
        }

        constraintValidatorContext.buildConstraintViolationWithTemplate(
                        passwordValidator.getMessages(result)
                                .stream()
                                .findFirst()
                                .get())
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
