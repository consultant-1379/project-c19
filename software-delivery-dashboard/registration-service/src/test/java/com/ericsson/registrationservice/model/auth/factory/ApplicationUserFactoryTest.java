package com.ericsson.registrationservice.model.auth.factory;

import com.ericsson.registrationservice.model.auth.ApplicationUser;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationUserFactoryTest {

  private ApplicationUser applicationUser;

  private static final String EMAIL = "test@email.com";

  private static final String FIRSTNAME = "Elvis";

  private static final String LASTNAME = "Presley";

  private static final String PASSWORD = "password";

  @BeforeEach
  public void setUp(){
    applicationUser = ApplicationUserFactory.generateApplicationUser(EMAIL, FIRSTNAME, LASTNAME, PASSWORD);
  }

  @Test
  void testUserEmail(){
    assertEquals(EMAIL, applicationUser.getEmail());
  }

  @Test
  void testUserFirstName(){
    assertEquals(FIRSTNAME, applicationUser.getFirstName());
  }

  @Test
  void testUserLastName(){
    assertEquals(LASTNAME, applicationUser.getLastName());
  }

  @Test
  void testUserPassword(){
    assertEquals(PASSWORD, applicationUser.getPassword());
  }

  @Test
  void testIsEnabled(){
    assertFalse(applicationUser.isEnabled());
  }

  @Test
  void testIsAccountNonExpired(){
    assertTrue(applicationUser.isAccountNonExpired());
  }

  @Test
  void testIsCredentialsNonExpired(){
    assertTrue(applicationUser.isCredentialsNonExpired());
  }

  @Test
  void testIsAccountNonLocked(){
    assertTrue(applicationUser.isAccountNonLocked());
  }

  @Test
  void testGrantedAuthorities(){
    assertEquals(List.of(new SimpleGrantedAuthority("USER")), applicationUser.getGrantedAuthorities());
  }

  @Test
  void testPasswordResetTokenNotNull(){
    assertNotNull(applicationUser.getPasswordResetVerificationToken());
  }

  @Test
  void testRegistrationTokenNotNull(){
    assertNotNull(applicationUser.getRegistrationVerificationToken());
  }

  @Test
  void testRegistrationTokenExpiry(){
    assertTrue(ZonedDateTime.now().plusMinutes(15L).isAfter(applicationUser.getRegistrationVerificationToken().getExpiryDateTime()));
  }

}