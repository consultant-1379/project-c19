package com.ericsson.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for email service application.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@SpringBootApplication
public class EmailServiceApplication {

  public static void main(final String[] args) {
    SpringApplication.run(EmailServiceApplication.class, args);
  }

}
