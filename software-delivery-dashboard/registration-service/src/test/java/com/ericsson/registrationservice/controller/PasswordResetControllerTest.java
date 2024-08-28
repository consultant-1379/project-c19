package com.ericsson.registrationservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.registrationservice.dto.PasswordResetDto;
import com.ericsson.registrationservice.service.PasswordResetService;
import com.ericsson.registrationservice.service.PasswordResetService.PasswordResetStatus;
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
class PasswordResetControllerTest {

  private static final String VALID_EMAIL = "johndoe@ericsson.com";
  private static final String INVALID_EMAIL = "johndoe@mail.com";
  private static final String PASSWORD = "password";
  private static final String ALTERNATIVE_PASSWORD = "password1";

  private static final String TOKEN = UUID.randomUUID().toString();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PasswordResetService passwordResetService;

  @Test
  void whenPostPasswordReset_andValidEmail_thenStatusOkAndPasswordResetStatusAwaitingEmailVerification()
      throws Exception {
    Mockito
        .when(this.passwordResetService.requestPasswordReset(VALID_EMAIL))
        .thenReturn(PasswordResetStatus.AWAITING_EMAIL_VERIFICATION);

    this.mockMvc
        .perform(
            post(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI)
                .param("email", VALID_EMAIL)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.password_reset_status")
                .value(PasswordResetStatus.AWAITING_EMAIL_VERIFICATION.toString())
        );
  }

  @Test
  void whenPostPasswordReset_andInvalidEmail_thenReturnStatusOkAndPasswordResetStatusPasswordsDoNotMatch()
      throws Exception {
    Mockito
        .when(this.passwordResetService.requestPasswordReset(INVALID_EMAIL))
        .thenReturn(PasswordResetStatus.FAILURE);

    this.mockMvc
        .perform(
            post(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI)
                .param("email", INVALID_EMAIL)
        )
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.password_reset_status")
                .value(PasswordResetStatus.FAILURE.toString())
        );
  }

  @Test
  void whenPostPasswordResetConfirm_validEmailAndPasswordDto_thenReturnStatusOkAndPasswordResetStatusSuccess()
      throws Exception {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto))
        .thenReturn(PasswordResetStatus.SUCCESS);

    this.mockMvc
        .perform(
            post(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(passwordResetDto))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.password_reset_status")
                .value(PasswordResetStatus.SUCCESS.toString())
        );
  }

  @Test
  void whenPostPasswordResetConfirm_passwordResetTokenExpired_thenReturnStatusBadRequestAndPasswordResetStatusEmailVerifiationExpired()
      throws Exception {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto))
        .thenReturn(PasswordResetStatus.EMAIL_VERIFICATION_EXPIRED);

    this.mockMvc
        .perform(
            post(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(passwordResetDto))
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.password_reset_status")
                .value(PasswordResetStatus.EMAIL_VERIFICATION_EXPIRED.toString())
        );
  }

  @Test
  void whenPostPasswordResetConfirm_passwordsDoNotMatch_thenReturnStatusBadRequestAndPasswordResetStatusPasswordsDoNotMatch()
      throws Exception {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(ALTERNATIVE_PASSWORD)
        .build();

    Mockito
        .when(this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto))
        .thenReturn(PasswordResetStatus.PASSWORDS_DO_NOT_MATCH);

    this.mockMvc
        .perform(
            post(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(passwordResetDto))
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.password_reset_status")
                .value(PasswordResetStatus.PASSWORDS_DO_NOT_MATCH.toString())
        );
  }

  @Test
  void whenPostPasswordResetConfirm_passwordResetFailure_thenReturnStatusInternalServerErrorAndPasswordResetStatusFailure()
      throws Exception {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto))
        .thenReturn(PasswordResetStatus.FAILURE);

    this.mockMvc
        .perform(
            post(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(passwordResetDto))
        )
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.password_reset_status")
                .value(PasswordResetStatus.FAILURE.toString())
        );
  }

}
