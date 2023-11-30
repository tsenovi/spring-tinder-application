package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.exceptions.EmailAlreadyVerifiedException;
import com.volasoftware.tinder.exceptions.VerificationTokenExpiredException;
import com.volasoftware.tinder.exceptions.VerificationTokenNotExistException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.VerificationTokenRepository;
import com.volasoftware.tinder.services.contracts.EmailService;
import com.volasoftware.tinder.services.contracts.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.volasoftware.tinder.constants.Constants;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void generateToken(Account account) {
        String uuidToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                uuidToken,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                account
        );

        verificationTokenRepository.save(verificationToken);

        emailService.send(account.getEmail(), account.getFirstName(), uuidToken);
    }

    @Override
    public AccountDto verifyToken(String token) {

        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByToken(token);
        if (optionalVerificationToken.isEmpty()) {
            throw new VerificationTokenNotExistException(Constants.TOKEN_NOT_EXIST);
        }

        VerificationToken verificationToken = optionalVerificationToken.get();
        if (verificationToken.getVerifiedAt() != null) {
            throw new EmailAlreadyVerifiedException(Constants.EMAIL_ALREADY_CONFIRMED);
        }

        LocalDateTime expiresAt = verificationToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException(Constants.TOKEN_EXPIRED);
        }

        verificationToken.setVerifiedAt(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);
        return AccountMapper.INSTANCE.accountToAccountDto(verificationToken.getAccount());
    }
}
