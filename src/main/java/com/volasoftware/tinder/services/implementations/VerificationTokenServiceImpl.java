package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.VerificationTokenRepository;
import com.volasoftware.tinder.services.contracts.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public void register(Account account){

        String uuidToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                uuidToken,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plus(2, ChronoUnit.DAYS),
                account
        );

        verificationTokenRepository.save(verificationToken);
    }

}
