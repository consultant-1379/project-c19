package com.ericsson.registrationservice.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class RegistrationDtoTest {

  private RegistrationDto registrationDto;
  private final String firstName = "John";
  private final String lastName = "Doe";
  private final String email = "johndoe@ericsson.com";
  private final String password = "password";
  private final String confirmPassword = "password";

  @BeforeEach
  void beforeEach() {
    this.registrationDto = RegistrationDto
        .builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .password(password)
        .confirmPassword(confirmPassword)
        .build();
  }

  @Test
  void firstNameGetter() {
    Assertions.assertEquals(firstName, registrationDto.getFirstName());
  }

  @Test
  void secondNameGetter() {
    Assertions.assertEquals(lastName, registrationDto.getLastName());
  }

  @Test
  void emailGetter() {
    Assertions.assertEquals(email, registrationDto.getEmail());
  }

  @Test
  void passwordGetter() {
    Assertions.assertEquals(password, registrationDto.getPassword());
  }

  @Test
  void confirmPasswordGetter() {
      Assertions.assertEquals(confirmPassword, registrationDto.getConfirmPassword());
  }

}
