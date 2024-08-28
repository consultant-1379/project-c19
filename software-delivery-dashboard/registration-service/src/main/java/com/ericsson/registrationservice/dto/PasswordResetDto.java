package com.ericsson.registrationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Password reset data transfer object used when resetting user passwords.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@Builder
@Data
public class PasswordResetDto {

  private String password;

  @JsonProperty("confirm_password")
  private String confirmPassword;

}
