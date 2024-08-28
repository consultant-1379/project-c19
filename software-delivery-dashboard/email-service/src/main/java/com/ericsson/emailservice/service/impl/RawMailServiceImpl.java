package com.ericsson.emailservice.service.impl;

import com.ericsson.emailservice.service.RawMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Implementation of raw mail service, used to send mails using java mail sender directly.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RawMailServiceImpl implements RawMailService {

  private final MailSender mailSender;

  @Override
  public MailSendStatus sendMail(final String addressTo, final String subject, final String text) {
    final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(addressTo);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setText(text);

    MailSendStatus mailSendStatus = MailSendStatus.FAILURE;

    try {
      this.mailSender.send(simpleMailMessage);
      mailSendStatus = MailSendStatus.SENT;
      log.info("E-mail sent with status: {}", mailSendStatus);
    } catch (MailException ex) {
      log.error(ex.getMessage());
    }

    return mailSendStatus;
  }

}
