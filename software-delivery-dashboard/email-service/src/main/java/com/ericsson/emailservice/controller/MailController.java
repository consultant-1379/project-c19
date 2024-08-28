package com.ericsson.emailservice.controller;

import com.ericsson.emailservice.dto.MailDto;
import com.ericsson.emailservice.dto.MailResponseDto;
import com.ericsson.emailservice.service.RawMailService;
import com.ericsson.emailservice.service.RawMailService.MailSendStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic mail controller, single endpoint allowing for emails to be sent.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(MailController.MAIL_CONTROLLER_BASE_URI)
public class MailController {

  public static final String MAIL_CONTROLLER_BASE_URI = "/mail";

  private final RawMailService rawMailService;

  /**
   * Single endpoint using mail data transfer object as request body for sending mail using the raw
   * mail service.
   *
   * @param mailDto mail data transfer object containing address, subject, and e-mail text.
   * @return response entity containing mail response data transfer object.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MailResponseDto> postMail(@RequestBody final MailDto mailDto) {
    final MailResponseDto mailResponseDto = new MailResponseDto(
        this.rawMailService.sendMail(mailDto.getAddressTo(), mailDto.getSubject(),
            mailDto.getText())
    );

    return (mailResponseDto.getMailSendStatus() == MailSendStatus.SENT)
        ? ResponseEntity.ok(mailResponseDto)
        : ResponseEntity.internalServerError().body(mailResponseDto);
  }

}
