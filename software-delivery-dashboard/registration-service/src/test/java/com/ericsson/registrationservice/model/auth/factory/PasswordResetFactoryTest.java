package com.ericsson.registrationservice.model.auth.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordResetFactoryTest {

  private PasswordResetVerificationToken validPasswordResetVerificationToken;
  private PasswordResetVerificationToken expiredPasswordResetVerificationToken;

  @BeforeEach
  void setUp(){
    validPasswordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();
    expiredPasswordResetVerificationToken = PasswordResetFactory.generateExpiredToken();
  }

  @Test
  void testValidExpiryTime(){
    assertTrue(ZonedDateTime.now().plusMinutes(15L).isAfter(validPasswordResetVerificationToken.getExpiryDateTime()));
  }

  @Test
  void testValidIDIsNotNull(){
    assertNotNull(validPasswordResetVerificationToken.getId());
  }

  @Test
  void testValidTokenIsNotNull(){
    assertNotNull(validPasswordResetVerificationToken.getToken());
  }

  @Test
  void testExpiredExpiryTime(){
    assertTrue(ZonedDateTime.now().isAfter(expiredPasswordResetVerificationToken.getExpiryDateTime()));
  }

  @Test
  void testExpiredIDIsNotNull(){
    assertNotNull(expiredPasswordResetVerificationToken.getId());
  }

  @Test
  void testExpiredTokenIsNotNull(){
    assertNotNull(expiredPasswordResetVerificationToken.getToken());
  }
}