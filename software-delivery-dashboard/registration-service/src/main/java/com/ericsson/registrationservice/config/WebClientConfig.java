package com.ericsson.registrationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration of web clients, in this case only one client is required in order to access the
 * e-mail service.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Configuration
public class WebClientConfig {

  public static final String EMAIL_SERVICE_WEB_CLIENT_QUALIFIER = "emailServiceWebClient";

  public static final String EMAIL_SERVICE_BASE_URL = "http://localhost:8081";

  @Bean(name = WebClientConfig.EMAIL_SERVICE_WEB_CLIENT_QUALIFIER)
  public WebClient webClient() {
    return WebClient
        .builder()
        .baseUrl(WebClientConfig.EMAIL_SERVICE_BASE_URL)
        .build();
  }

}
