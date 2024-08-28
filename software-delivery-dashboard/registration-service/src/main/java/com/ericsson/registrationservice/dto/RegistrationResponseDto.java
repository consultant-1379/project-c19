package com.ericsson.registrationservice.dto;

import com.ericsson.registrationservice.service.RegistrationService.RegistrationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Registration response data transfer object used to respond with user registration status.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@AllArgsConstructor
@Data
public class RegistrationResponseDto {

  @JsonProperty("registration_status")
  private RegistrationStatus registrationStatus;

}
