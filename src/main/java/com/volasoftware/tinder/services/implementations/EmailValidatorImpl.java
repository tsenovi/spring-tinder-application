package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.services.contracts.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailValidatorImpl implements EmailValidator {
    @Override
    public boolean test(String string) {

        //TODO Implement regex for the check!
        return true;
    }
}
