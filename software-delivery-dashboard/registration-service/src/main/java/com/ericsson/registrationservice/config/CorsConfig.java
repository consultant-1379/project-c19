package com.ericsson.registrationservice.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Value("${custom.client-service-base-url}")
  private String clientServiceBaseUrl;

  @Value("${custom.docker-client-service-base-url}")
  private String dockerClientServiceBaseUrl;

  @Bean
  public CorsFilter corsFilter() {
    final CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedOrigins(
        List.of(this.clientServiceBaseUrl, this.dockerClientServiceBaseUrl));
    corsConfiguration.setAllowedHeaders(List.of(
        "Origin",
        "Access-Control-Allow-Origin",
        "Content-Type",
        "Accepts",
        "Authorization",
        "Origin, Accept",
        "X-Requested-With",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"
    ));
    corsConfiguration.setAllowedMethods(List.of(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));

    final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

    return new CorsFilter(urlBasedCorsConfigurationSource);
  }

}
