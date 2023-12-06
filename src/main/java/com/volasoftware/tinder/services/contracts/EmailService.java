package com.volasoftware.tinder.services.contracts;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void sendVerificationMail(String receiverEmail, String receiverFirstName, String token);
}
