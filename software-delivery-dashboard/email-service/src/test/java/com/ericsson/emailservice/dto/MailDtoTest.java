package com.ericsson.emailservice.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MailDtoTest {

  private final String addressTo = "johndoe@ericsson.com";
  private final String subject = "RE Testing";
  private final String text = "text about testing";
  private MailDto mailDto;

  @BeforeEach
  void beforeEach() {
    this.mailDto = MailDto
        .builder()
        .addressTo(addressTo)
        .subject(subject)
        .text(text)
        .build();
  }

  @Test
  void addresstoGetter() {
    Assertions.assertEquals(addressTo, mailDto.getAddressTo());
  }

  @Test
  void subjectGetter() {
    Assertions.assertEquals(subject, mailDto.getSubject());
  }

  @Test
  void textGetter() {
    Assertions.assertEquals(text, mailDto.getText());
  }


}
