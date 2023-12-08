package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.constants.SecurityConstant;
import com.volasoftware.tinder.exceptions.EmailAlreadyVerifiedException;
import com.volasoftware.tinder.exceptions.VerificationTokenExpiredException;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.VerificationTokenRepository;
import com.volasoftware.tinder.services.contracts.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public VerificationToken generateToken(Account account) {
        String uuidToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                uuidToken,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(SecurityConstant.TOKEN_EXPIRATION_DAYS),
                account
        );

        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public Account verifyToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenExpiredException(SecurityConstant.TOKEN_EXPIRED));

        isEmailAlreadyVerified(verificationToken);

        LocalDateTime tokenExpirationDate = verificationToken.getExpiresAt();
        isTokenExpired(tokenExpirationDate);

        verificationToken.setVerifiedAt(LocalDateTime.now());
        VerificationToken updatedVerificationToken = verificationTokenRepository.save(verificationToken);
        return updatedVerificationToken.getAccount();
    }

    private void isTokenExpired(LocalDateTime expiresAt) {
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException(SecurityConstant.TOKEN_EXPIRED);
        }
    }

    private void isEmailAlreadyVerified(VerificationToken verificationToken) {
        if (verificationToken.getVerifiedAt() != null) {
            throw new EmailAlreadyVerifiedException(MailConstant.ALREADY_CONFIRMED);
        }
    }
}
