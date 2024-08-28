package com.ericsson.registrationservice.model.auth.factory;

import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PasswordResetFactory {

  private PasswordResetFactory(){}

  public static PasswordResetVerificationToken generateValidPasswordToken(){
      return PasswordResetVerificationToken.builder().id(-1L).token(UUID.randomUUID().toString()).expiryDateTime(
          ZonedDateTime.now().plusMinutes(15L)).build();
  }

  public static PasswordResetVerificationToken generateExpiredToken(){
    return PasswordResetVerificationToken.builder().id(-1L).token(UUID.randomUUID().toString()).expiryDateTime(
        ZonedDateTime.now()).build();
  }
}
