package com.ericsson.registrationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Registration data transfer object used when registering new users.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Builder
@Data
public class RegistrationDto {

  private String email;

  @JsonProperty("firstname")
  private String firstName;

  @JsonProperty("lastname")
  private String lastName;

  private String password;

  @JsonProperty("confirm_password")
  private String confirmPassword;

}
