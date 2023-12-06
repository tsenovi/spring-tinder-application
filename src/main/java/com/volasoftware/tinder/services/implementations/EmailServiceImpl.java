package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.services.contracts.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    @Override
    @Async
    public void sendVerificationMail(String receiverEmail, String receiverFirstName, String token) {
        String email = buildVerificationMail(receiverFirstName, token);

        send(receiverEmail, email);
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

    private String buildVerificationMail(String name, String token) {
        String link = baseUrl + verifyUrl + token;

        byte[] contentBytes = readFile(MailConstant.VERIFICATION_FILE);

        String htmlContent = new String(contentBytes);
        String htmlWithLink = htmlContent.replace(MailConstant.ACTIVATION_LINK, link);

        return htmlWithLink;
    }

    private byte[] readFile(String filePath) {
        Resource resource = new ClassPathResource(filePath);

        byte[] contentBytes;
        try {
            contentBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return contentBytes;
    }
}
