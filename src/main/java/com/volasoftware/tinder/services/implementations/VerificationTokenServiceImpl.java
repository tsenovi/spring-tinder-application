package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.dtos.AccountDto;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;
import com.volasoftware.tinder.repositories.VerificationTokenRepository;
import com.volasoftware.tinder.services.contracts.EmailService;
import com.volasoftware.tinder.services.contracts.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        //TODO create those exceptions!

        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByToken(token);
        if (optionalVerificationToken.isEmpty()) {
//            throw new VerificationTokenNotExistException("Verification token does not exist!");
        }

        VerificationToken verificationToken = optionalVerificationToken.get();
        if (verificationToken.getVerifiedAt() != null) {
//            throw new EmailAlreadyVerifiedException("Email already confirmed!");
        }

        LocalDateTime expiresAt = verificationToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())) {
//            throw new VerificationTokenExpiredException("Verification token expired!");
        }

        verificationTokenRepository.updateToken(token, LocalDateTime.now());
        return AccountMapper.INSTANCE.accountToAccountDto(verificationToken.getAccount());
    }
}
