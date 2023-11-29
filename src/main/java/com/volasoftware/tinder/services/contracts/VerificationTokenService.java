package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.models.Account;

public interface VerificationTokenService {
    void register(Account token);
}
