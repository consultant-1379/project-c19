package com.ericsson.clientservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Configuration
public class WebClientConfig {

  @Value("${custom.jenkins-service-base-url}")
  private String jenkinsServiceBaseUrl;

  @Bean
  public WebClient jenkinsServiceWebClient() {
    return WebClient.builder()
        .baseUrl(this.jenkinsServiceBaseUrl)
        .build();
  }

}
