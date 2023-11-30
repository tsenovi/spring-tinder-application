package com.volasoftware.tinder.services.contracts;

import java.util.function.Predicate;

public interface EmailValidator extends Predicate<String> {
    boolean test(String string);
}
