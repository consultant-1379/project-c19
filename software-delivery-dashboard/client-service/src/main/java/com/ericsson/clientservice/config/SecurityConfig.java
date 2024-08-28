package com.ericsson.clientservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Basic security configuration, allows all requests relying on Docker configuration to manage
 * network access.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;

  @Value("${custom.login-path}")
  private String loginPath;

  /**
   * Overriding configure method to provide security configuration.
   *
   * @param http HttpSecurity object used to configure security.
   * @throws Exception possible exception.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/css/**", this.loginPath, "/login_error", "/register", "/about").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage(this.loginPath)
        .loginProcessingUrl(this.loginPath)
        .defaultSuccessUrl("/", true)
        .failureUrl("/login_error")
        .and()
        .logout().permitAll()
        .logoutUrl("/logout")
        .deleteCookies("JSESSIONID");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
