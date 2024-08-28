package com.ericsson.registrationservice.model.auth.factory;

import com.ericsson.registrationservice.model.auth.RegistrationVerificationToken;
import java.time.ZonedDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationFactoryTest {

  private RegistrationVerificationToken registrationVerificationToken;

  @BeforeEach
  void setUp(){
    registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
  }

  @Test
  void testExpiryTime(){
    assertTrue(ZonedDateTime.now().plusMinutes(15L).isAfter(registrationVerificationToken.getExpiryDateTime()));
  }

  @Test
  void testIDIsNotNull(){
    assertNotNull(registrationVerificationToken.getId());
  }

  @Test
  void testTokenIsNotNull(){
    assertNotNull(registrationVerificationToken.getToken());
  }

}