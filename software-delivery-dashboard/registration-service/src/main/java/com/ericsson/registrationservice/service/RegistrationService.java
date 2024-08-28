package com.ericsson.registrationservice.service;

import com.ericsson.registrationservice.dto.RegistrationDto;

/**
 * Service used to handle registering new users.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface RegistrationService {

  String REGISTRATION_SERVICE_BASE_URL = "http://localhost:8082";

  RegistrationStatus registerUser(final RegistrationDto registrationDto);

  RegistrationStatus confirmUserRegistration(final String registrationVerificationTokenValue);

  enum RegistrationStatus {
    FAILURE,
    AWAITING_EMAIL_VERIFICATION,
    USER_ALREADY_EXISTS,
    INVALID_EMAIL_DOMAIN,
    PASSWORDS_DO_NOT_MATCH,
    EMAIL_VERIFICATION_EXPIRED,
    SUCCESS
  }

}
