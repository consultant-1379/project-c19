package com.ericsson.registrationservice.model.auth.factory;

import com.ericsson.registrationservice.model.auth.RegistrationVerificationToken;
import java.time.ZonedDateTime;
import java.util.UUID;

public class RegistrationFactory {


  private RegistrationFactory(){}

  public static RegistrationVerificationToken generateRegistrationToken(){
    return RegistrationVerificationToken.builder().id(-1L).token(UUID.randomUUID().toString()).expiryDateTime(
        ZonedDateTime.now().plusMinutes(15L)).build();
  }

}
