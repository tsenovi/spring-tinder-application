package com.volasoftware.tinder.services.contracts;

import com.volasoftware.tinder.models.Account;
import com.volasoftware.tinder.models.VerificationToken;

public interface VerificationTokenService {

    VerificationToken generateToken(Account account);

    Account verifyToken(String token);

    VerificationToken regenerateToken(Account account);
}
