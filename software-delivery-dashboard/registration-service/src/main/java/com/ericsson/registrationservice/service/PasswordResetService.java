package com.ericsson.registrationservice.service;

import com.ericsson.registrationservice.dto.PasswordResetDto;

/**
 * Service used to handle resetting user passwords.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface PasswordResetService {

  PasswordResetStatus requestPasswordReset(final String email);

  PasswordResetStatus confirmPasswordReset(
      final String passwordResetTokenValue,
      final PasswordResetDto passwordResetDto
  );

  enum PasswordResetStatus {
    FAILURE,
    AWAITING_EMAIL_VERIFICATION,
    PASSWORDS_DO_NOT_MATCH,
    EMAIL_VERIFICATION_EXPIRED,
    SUCCESS
  }

}
