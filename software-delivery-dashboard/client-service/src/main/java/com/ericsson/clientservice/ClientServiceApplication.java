package com.ericsson.clientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {
    "com.ericsson.registrationservice.model",
    "com.ericsson.clientservice.model"
})
@SpringBootApplication
public class ClientServiceApplication {

  public static void main(final String[] args) {
    SpringApplication.run(ClientServiceApplication.class, args);
  }

}
