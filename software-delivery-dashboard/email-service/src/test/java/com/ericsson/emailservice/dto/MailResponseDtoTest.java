package com.ericsson.emailservice.dto;


import com.ericsson.emailservice.service.RawMailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MailResponseDtoTest {

  private final MailResponseDto mailResponseDto = new MailResponseDto(
      RawMailService.MailSendStatus.FAILURE);
  private final RawMailService.MailSendStatus mailSendStatus =
      RawMailService.MailSendStatus.FAILURE;

  @Test
  void passwordResetStatusGetter() {
    Assertions.assertEquals(mailSendStatus, mailResponseDto.getMailSendStatus());
  }
}