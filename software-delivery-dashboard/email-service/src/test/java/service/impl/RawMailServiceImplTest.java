package service.impl;

import static org.mockito.ArgumentMatchers.any;

import com.ericsson.emailservice.service.RawMailService;
import com.ericsson.emailservice.service.RawMailService.MailSendStatus;
import com.ericsson.emailservice.service.impl.RawMailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;


class RawMailServiceImplTest {

  private final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
  private RawMailServiceImpl rawMailService;
  private MailSender mailSender;
  private String addressTo;
  private String subject;
  private String text;
  private RawMailService mailService;
  private RawMailService.MailSendStatus mailSendStatus;

  @BeforeEach
  void beforeEach() {
    this.addressTo = "vibhu.goel@ericsson.com";
    this.subject = "This test Works?";
    this.text = "yes";

    this.mailSender = Mockito.mock
        (MailSender.class);

    this.rawMailService = new RawMailServiceImpl(this.mailSender);
    this.simpleMailMessage.setTo(this.addressTo);
    this.simpleMailMessage.setSubject(this.subject);
    this.simpleMailMessage.setText(this.text);

  }

  @Test
  void whenMailSendStatusIsSent() {
    Assertions.assertEquals(MailSendStatus.SENT,
        this.rawMailService.sendMail(this.addressTo, this.subject, this.text));
    Mockito.verify(this.mailSender, Mockito.atLeastOnce()).send(this.simpleMailMessage);
  }

  @Test
  void whenMailSendStatusIsFail() {
    Mockito.doThrow(MailSendException.class).when(this.mailSender)
        .send(any(SimpleMailMessage.class));
    Assertions.assertEquals(MailSendStatus.FAILURE,
        this.rawMailService.sendMail(this.addressTo, this.subject, this.text));
    Mockito.verify(this.mailSender, Mockito.atLeastOnce()).send(this.simpleMailMessage);
  }
}