package com.ericsson.registrationservice.model.auth;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Builder
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class ApplicationUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String firstName;

  private String lastName;

  private String password;

  private boolean isEnabled;

  private boolean isAccountNonExpired;

  private boolean isCredentialsNonExpired;

  private boolean isAccountNonLocked;

  @ElementCollection(fetch = FetchType.EAGER)
  private Collection<GrantedAuthority> grantedAuthorities;

  @OneToOne
  private RegistrationVerificationToken registrationVerificationToken;

  @OneToOne
  private PasswordResetVerificationToken passwordResetVerificationToken;

}
