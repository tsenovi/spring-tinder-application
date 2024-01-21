package com.volasoftware.tinder.utils;

import com.volasoftware.tinder.constants.AccountType;
import com.volasoftware.tinder.constants.Gender;
import com.volasoftware.tinder.constants.Role;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.Location;
import com.volasoftware.tinder.repositories.AccountRepository;
import com.volasoftware.tinder.repositories.LocationRepository;
import com.volasoftware.tinder.utils.contracts.NameGenerator;
import com.volasoftware.tinder.utils.contracts.PasswordGenerator;
import jakarta.annotation.PostConstruct;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BotInitializer {

    public static final int MAX_BOTS_COUNT = 20;

    public static final int MAX_AGE_BOUNDARY = 82;

    private static final int MIN_AGE_BOUNDARY = 18;

    public static final double VRATSA_LATITUDE = 43.204639;

    public static final double VRATSA_LONGITUDE = 23.548651;

    public static final String EXAMPLE_COM = "@example.com";

    private final AccountRepository accountRepository;

    private final LocationRepository locationRepository;

    private final Random random;

    private final PasswordEncoder passwordEncoder;

    private final PasswordGenerator passwordGenerator;

    private final NameGenerator nameGenerator;

    @PostConstruct
    void initialize() {
        Page<Account> botAccounts = accountRepository.findByAccountType(
            AccountType.BOT,
            PageRequest.of(0, 10)
        );

        if (botAccounts.isEmpty()) {
            for (int i = 0; i < MAX_BOTS_COUNT; i++) {
                generateBotAccount();
            }
        }
    }

    @Transactional
    private void generateBotAccount() {
        Account account = new Account();
        String firstName = nameGenerator.generateFirstName();
        String lastName = nameGenerator.generateLastName();

        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(firstName + lastName + EXAMPLE_COM);
        account.setPassword(generateSecuredRandomPassword());
        account.setGender(Gender.values()[random.nextInt(Gender.values().length)]);
        account.setRole(Role.USER);
        account.setLocked(false);
        account.setVerified(true);
        account.setAge(MIN_AGE_BOUNDARY + random.nextInt(MAX_AGE_BOUNDARY));
        account.setAccountType(AccountType.BOT);

        Account savedAccount = accountRepository.save(account);

        Location location = new Location();
        location.setAccount(savedAccount);
        location.setLatitude(VRATSA_LATITUDE + random.nextDouble());
        location.setLongitude(VRATSA_LONGITUDE + random.nextDouble());
        Location savedLocation = locationRepository.save(location);

        savedAccount.setLocation(savedLocation);
        accountRepository.save(savedAccount);
    }

    private String generateSecuredRandomPassword() {
        String password = passwordGenerator.generateRandomPassword();
        return passwordEncoder.encode(password);
    }
}
