package com.ericsson.clientservice.service.impl;

import com.ericsson.clientservice.repository.ApplicationUserRepository;
import com.ericsson.registrationservice.model.auth.ApplicationUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final ApplicationUserRepository applicationUserRepository;

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final ApplicationUser applicationUser = this.applicationUserRepository.findByEmail(username);
    if (applicationUser == null) {
      throw new UsernameNotFoundException("No username found for: " + username);
    }

    return new org.springframework.security.core.userdetails.User(
        applicationUser.getEmail(),
        applicationUser.getPassword(),
        applicationUser.isEnabled(),
        applicationUser.isAccountNonExpired(),
        applicationUser.isCredentialsNonExpired(),
        applicationUser.isAccountNonLocked(),
        applicationUser.getGrantedAuthorities()
    );
  }

}
