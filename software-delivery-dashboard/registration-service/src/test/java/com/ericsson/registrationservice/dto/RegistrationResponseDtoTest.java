package com.ericsson.registrationservice.dto;

import com.ericsson.registrationservice.service.RegistrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class RegistrationResponseDtoTest {

  private final RegistrationResponseDto registrationResponseDto = new RegistrationResponseDto(
      RegistrationService.RegistrationStatus.FAILURE);

  private final RegistrationService.RegistrationStatus registrationStatus =
      RegistrationService.RegistrationStatus.FAILURE;

  @Test
  void passwordResetStatusGetter() {
    Assertions.assertEquals(registrationStatus, registrationResponseDto.getRegistrationStatus());
  }
  
}