package com.ericsson.registrationservice.dto;

import com.ericsson.registrationservice.service.PasswordResetService.PasswordResetStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Password reset response data transfer object used to response with password reset status.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@AllArgsConstructor
@Data
public class PasswordResetResponseDto {

  @JsonProperty("password_reset_status")
  private PasswordResetStatus passwordResetStatus;

}
