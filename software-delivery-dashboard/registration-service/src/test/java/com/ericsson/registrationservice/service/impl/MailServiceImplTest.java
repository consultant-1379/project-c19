package com.ericsson.registrationservice.service.impl;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

class MailServiceImplTest {

  private MailServiceImpl mailService;

  private WebClient mockWebClient;

  private  MailServiceImpl mockMailService = mock(MailServiceImpl.class);

  private static final String ADDRESS = "email@email.com";

  private static final String SUBJECT = "This is the subject";

  private static final String TEXT = "This is the text";

  @BeforeEach
  void beforeEach() {
    this.mockWebClient = Mockito.mock(WebClient.class);

    this.mailService = new MailServiceImpl(this.mockWebClient);
  }

  @Test
  void whenMailServiceImpl_thenNotNull() {
    Assertions.assertNotNull(this.mailService);
  }

  @Test
  void testSendMail(){
    mockMailService.sendMail(ADDRESS,SUBJECT,TEXT);
    verify(mockMailService, atLeastOnce()).sendMail(ADDRESS, SUBJECT, TEXT);
  }

}