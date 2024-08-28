package com.ericsson.registrationservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ericsson.registrationservice.dto.RegistrationDto;
import com.ericsson.registrationservice.service.RegistrationService;
import com.ericsson.registrationservice.service.RegistrationService.RegistrationStatus;
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
class RegistrationControllerTest {

  private static final String VALID_EMAIL = "johndoe@ericsson.com";
  private static final String INVALID_EMAIL = "johndoe@mail.com";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";
  private static final String PASSWORD = "password";
  private static final String ALTERNATIVE_PASSWORD = "password1";
  private static final String TOKEN = UUID.randomUUID().toString();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RegistrationService registrationService;

  @Test
  void whenPostRegister_andValidRegistrationDto_thenStatusOkAndRegistrationStatusAwaitingEmailVerification()
      throws Exception {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.registrationService.registerUser(registrationDto))
        .thenReturn(RegistrationStatus.AWAITING_EMAIL_VERIFICATION);

    this.mockMvc
        .perform(
            post(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationDto))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.AWAITING_EMAIL_VERIFICATION.toString())
        );
  }

  @Test
  void whenPostRegister_andInvalidEmail_thenStatusBadRequestAndRegistrationStatusInvalidEmailDomain()
      throws Exception {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(INVALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.registrationService.registerUser(registrationDto))
        .thenReturn(RegistrationStatus.INVALID_EMAIL_DOMAIN);

    this.mockMvc
        .perform(
            post(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationDto))
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.INVALID_EMAIL_DOMAIN.toString())
        );
  }

  @Test
  void whenPostRegister_andPasswordsNotMatch_thenStatusBadRequestAndRegistrationStatusPasswordsDoNotMatch()
      throws Exception {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(ALTERNATIVE_PASSWORD)
        .build();

    Mockito
        .when(this.registrationService.registerUser(registrationDto))
        .thenReturn(RegistrationStatus.PASSWORDS_DO_NOT_MATCH);

    this.mockMvc
        .perform(
            post(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationDto))
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.PASSWORDS_DO_NOT_MATCH.toString())
        );
  }

  @Test
  void whenPostRegister_andEnabledUserExists_thenStatusConflictAndRegistrationStatusUserAlreadyExists()
      throws Exception {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.registrationService.registerUser(registrationDto))
        .thenReturn(RegistrationStatus.USER_ALREADY_EXISTS);

    this.mockMvc
        .perform(
            post(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationDto))
        )
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.USER_ALREADY_EXISTS.toString())
        );
  }

  @Test
  void whenPostRegister_andRegistrationFailure_thenStatusInternalServerErrorAndRegistrationStatusFailure()
      throws Exception {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito
        .when(this.registrationService.registerUser(registrationDto))
        .thenReturn(RegistrationStatus.FAILURE);

    this.mockMvc
        .perform(
            post(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationDto))
        )
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.FAILURE.toString())
        );
  }

  @Test
  void whenGetRegisterConfirm_andRegistrationSuccess_thenStatusOkAndRegistrationStatusSuccess()
      throws Exception {
    Mockito
        .when(this.registrationService.confirmUserRegistration(TOKEN))
        .thenReturn(RegistrationStatus.SUCCESS);

    this.mockMvc
        .perform(
            get(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.SUCCESS.toString())
        );
  }

  @Test
  void whenGetRegisterConfirm_andExpiredToken_thenStatusBadRequestAndRegistrationStatusEmailVerificationExpired()
      throws Exception {
    Mockito
        .when(this.registrationService.confirmUserRegistration(TOKEN))
        .thenReturn(RegistrationStatus.EMAIL_VERIFICATION_EXPIRED);

    this.mockMvc
        .perform(
            get(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.EMAIL_VERIFICATION_EXPIRED.toString())
        );
  }

  @Test
  void whenGetRegisterConfirm_andRegistrationFailure_thenStatusInternalServerErrorAndRegistrationStatusFailure()
      throws Exception {
    Mockito
        .when(this.registrationService.confirmUserRegistration(TOKEN))
        .thenReturn(RegistrationStatus.FAILURE);

    this.mockMvc
        .perform(
            get(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI + "/confirm")
                .param("token", TOKEN)
        )
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.registration_status")
                .value(RegistrationStatus.FAILURE.toString())
        );
  }

}
