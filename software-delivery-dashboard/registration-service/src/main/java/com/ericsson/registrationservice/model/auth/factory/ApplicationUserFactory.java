package com.ericsson.registrationservice.model.auth.factory;

import com.ericsson.registrationservice.model.auth.ApplicationUser;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ApplicationUserFactory {

  private ApplicationUserFactory() {
  }

  public static ApplicationUser generateApplicationUser(String email, String firstName,
      String lastName, String password) {
    return ApplicationUser.builder().id(-1L).email(email).firstName(firstName).lastName(lastName)
        .password(password).isEnabled(false).isAccountNonExpired(true).isCredentialsNonExpired(true)
        .isAccountNonLocked(true).grantedAuthorities(
            List.of(new SimpleGrantedAuthority("USER")))
        .registrationVerificationToken(RegistrationFactory.generateRegistrationToken())
        .passwordResetVerificationToken(PasswordResetFactory.generateExpiredToken()).build();
  }

}
