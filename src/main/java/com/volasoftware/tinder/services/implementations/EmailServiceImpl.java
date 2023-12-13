package com.volasoftware.tinder.services.implementations;

import com.volasoftware.tinder.constants.MailConstant;
import com.volasoftware.tinder.services.contracts.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final static Logger LOGGER = LoggerFactory
      .getLogger(EmailServiceImpl.class);

  private final JavaMailSender mailSender;

  @Override
  @Async
  public void send(String receiverEmail, String email) {
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
      LOGGER.error(MailConstant.SEND_ERROR, e);
      throw new IllegalStateException(MailConstant.SEND_ERROR);
    }
  }
}
