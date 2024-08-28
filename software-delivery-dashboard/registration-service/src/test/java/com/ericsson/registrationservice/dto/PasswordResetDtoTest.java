package com.ericsson.registrationservice.dto;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordResetDtoTest {

  private PasswordResetDto passwordResetDto;
  private final String password = "password";
  private final String confirmPassword = "password";

  @BeforeEach
  void beforeEach() {
    this.passwordResetDto = PasswordResetDto
        .builder()
        .password(password)
        .confirmPassword(confirmPassword)
        .build();
  }

  @Test
  void passwordGetter() {
    Assertions.assertEquals(password, passwordResetDto.getPassword());
  }

  @Test
  void confirmPasswordGetter() {
    Assertions.assertEquals(confirmPassword, passwordResetDto.getConfirmPassword());
  }

}
