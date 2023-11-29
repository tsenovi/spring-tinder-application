package com.volasoftware.tinder.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String string) {

        //TODO Implement regex for the check!
        return true;
    }
}
