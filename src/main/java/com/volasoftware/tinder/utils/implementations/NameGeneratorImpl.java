package com.volasoftware.tinder.utils.implementations;

import com.volasoftware.tinder.utils.contracts.NameGenerator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NameGeneratorImpl implements NameGenerator {

    private static final String[] firstNames = {"James", "John", "Jennifer", "Jessica", "Jacob",
        "Joshua", "Joseph", "Julia", "Jasmine", "Jared", "Joanna", "Jaden", "Jocelyn", "Jonah",
        "Jaxon", "Janelle", "Jacqueline", "Jett", "Jolene", "Justice"};

    private static final String[] lastNames = {"Jinnouchi", "Jin", "Jo", "Jouda", "Juba", "Junko",
        "Jabara", "Jabbar", "Jaber", "Jablon", "Jablonowski", "Jabs", "Jacaruso", "Jacek", "Jach",
        "Jacinto", "Jacobs", "Jacobson", "Jacques", "Jaimes"};

    private final Random random;

    @Override
    public String generateFirstName() {
        int index = random.nextInt(firstNames.length);
        return firstNames[index];
    }

    @Override
    public String generateLastName() {
        int index = random.nextInt(lastNames.length);
        return lastNames[index];
    }
}
