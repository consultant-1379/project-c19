package com.ericsson.registrationservice.service.impl;

import com.ericsson.emailservice.controller.MailController;
import com.ericsson.emailservice.dto.MailDto;
import com.ericsson.emailservice.dto.MailResponseDto;
import com.ericsson.registrationservice.config.WebClientConfig;
import com.ericsson.registrationservice.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Mail service used to access the e-mail microservice.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

  private final WebClient emailServiceWebClient;

  public MailServiceImpl(
      @Qualifier(WebClientConfig.EMAIL_SERVICE_WEB_CLIENT_QUALIFIER) final WebClient emailServiceWebClient
  ) {
    this.emailServiceWebClient = emailServiceWebClient;
  }

  @Async
  @Override
  public void sendMail(final String addressTo, final String subject, final String text) {
    final MailDto mailDto = MailDto
        .builder()
        .addressTo(addressTo)
        .subject(subject)
        .text(text)
        .build();

    final MailResponseDto mailResponseDto = this.emailServiceWebClient
        .post()
        .uri(WebClientConfig.EMAIL_SERVICE_BASE_URL + MailController.MAIL_CONTROLLER_BASE_URI)
        .bodyValue(mailDto)
        .retrieve()
        .bodyToMono(MailResponseDto.class)
        .block();

    if (mailResponseDto != null) {
      log.info("Attempted to send e-mail with status: {}", mailResponseDto.getMailSendStatus());
    } else {
      log.error("E-mail failed to send with status: null");
    }
  }

}
