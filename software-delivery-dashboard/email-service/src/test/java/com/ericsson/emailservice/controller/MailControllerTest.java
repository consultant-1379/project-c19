package com.ericsson.emailservice.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.emailservice.dto.MailDto;
import com.ericsson.emailservice.service.RawMailService;
import com.ericsson.emailservice.service.RawMailService.MailSendStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SpringBootTest
class MailControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RawMailService rawMailService;

  @Test
  void whenPostMailValidMailDto_thenReturnStatusOkAndMailSendStatusSent() throws Exception {
    final MailDto mailDto = MailDto
        .builder()
        .addressTo(UUID.randomUUID().toString())
        .subject(UUID.randomUUID().toString())
        .text(UUID.randomUUID().toString())
        .build();

    Mockito
        .when(this.rawMailService.sendMail(anyString(), anyString(), anyString()))
        .thenReturn(MailSendStatus.SENT);

    this.mockMvc
        .perform(
            post(MailController.MAIL_CONTROLLER_BASE_URI)
                .content(new ObjectMapper().writeValueAsString(mailDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.mail_send_status")
                .value(MailSendStatus.SENT.toString())
        );
  }

  @Test
  void whenPostMailInvalidMailDto_thenReturnStatusInternalServerErrorAndMailSendStatusFail()
      throws Exception {
    final MailDto mailDto = MailDto
        .builder()
        .addressTo(UUID.randomUUID().toString())
        .subject(UUID.randomUUID().toString())
        .text(UUID.randomUUID().toString())
        .build();

    Mockito
        .when(this.rawMailService.sendMail(anyString(), anyString(), anyString()))
        .thenReturn(MailSendStatus.FAILURE);

    this.mockMvc
        .perform(
            post(MailController.MAIL_CONTROLLER_BASE_URI)
                .content(new ObjectMapper().writeValueAsString(mailDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.mail_send_status")
                .value(MailSendStatus.FAILURE.toString())
        );
  }

}
