package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.models.Account;

public interface VerificationTokenService {
    void generateToken(Account account);

    Account verifyToken(String token);
}
