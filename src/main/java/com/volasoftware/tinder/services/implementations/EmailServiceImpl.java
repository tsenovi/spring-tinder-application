package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.services.contracts.EmailService;
import com.volasoftware.tinder.services.contracts.FileService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailServiceImpl.class);

    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.verify-url}")
    private String verifyUrl;

    private final JavaMailSender mailSender;

    private final FileService fileService;

    @Override
    @Async
    public void sendVerificationMail(String receiver, String token) {
        String link = baseUrl + verifyUrl + token;
        byte[] contentBytes = fileService.readHtml(MailConstant.VERIFICATION_FILE);
        String content = new String(contentBytes).replace(MailConstant.ACTIVATION_LINK, link);

        send(receiver, content);
    }

    private void send(String receiverEmail, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(receiverEmail);
            helper.setSubject("Confirm your email");
            helper.setFrom("ivan.tsenov@volasoftware.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
