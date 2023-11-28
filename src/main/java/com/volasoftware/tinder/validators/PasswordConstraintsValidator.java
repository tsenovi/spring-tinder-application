package com.volasoftware.tinder.validators;

import com.volasoftware.tinder.constraints.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

import org.passay.*;

public class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {

  @Override
  public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

    PasswordValidator passwordValidator = new PasswordValidator(
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
