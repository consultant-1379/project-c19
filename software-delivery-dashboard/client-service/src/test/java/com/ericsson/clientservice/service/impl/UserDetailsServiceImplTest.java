package com.ericsson.clientservice.service.impl;

import static org.mockito.ArgumentMatchers.anyString;

import com.ericsson.clientservice.repository.ApplicationUserRepository;
import com.ericsson.registrationservice.model.auth.ApplicationUser;
import com.ericsson.registrationservice.model.auth.factory.ApplicationUserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserDetailsServiceImplTest {

  private static final String USERNAME = "johndoe@ericsson.com";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";
  private static final String PASSWORD = "password";

  private UserDetailsServiceImpl userDetailsService;

  private ApplicationUserRepository applicationUserRepository;

  @BeforeEach
  void beforeEach() {
    this.applicationUserRepository = Mockito.mock(ApplicationUserRepository.class);

    this.userDetailsService = new UserDetailsServiceImpl(this.applicationUserRepository);
  }

  @Test
  void whenLoadByUsername_noUserExists_thenThrowUsernameNotFoundException() {
    Mockito
        .when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(null);

    Assertions.assertThrows(UsernameNotFoundException.class, () -> {
      this.userDetailsService.loadUserByUsername(USERNAME);
    });
  }

  @Test
  void whenLoadByUsername_validUser_thenSpringSecurityUserCreated() {
    final ApplicationUser applicationUser = ApplicationUserFactory
        .generateApplicationUser(
            USERNAME,
            FIRST_NAME,
            LAST_NAME,
            PASSWORD);

    Mockito
        .when(this.applicationUserRepository.findByEmail(USERNAME))
        .thenReturn(applicationUser);

    final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        applicationUser.getEmail(),
        applicationUser.getPassword(),
        applicationUser.isEnabled(),
        applicationUser.isAccountNonExpired(),
        applicationUser.isCredentialsNonExpired(),
        applicationUser.isAccountNonLocked(),
        applicationUser.getGrantedAuthorities()
    );

    Assertions.assertEquals(userDetails, this.userDetailsService.loadUserByUsername(USERNAME));
  }

}