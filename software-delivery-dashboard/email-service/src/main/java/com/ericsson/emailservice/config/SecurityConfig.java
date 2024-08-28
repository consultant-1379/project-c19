package com.ericsson.emailservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Basic security configuration, allows all requests relying on Docker configuration to manage
 * network access.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * Overriding configure method to provide security configuration.
   *
   * @param http HttpSecurity object used to configure security.
   * @throws Exception possible exception.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        .anyRequest().permitAll();
  }

}
