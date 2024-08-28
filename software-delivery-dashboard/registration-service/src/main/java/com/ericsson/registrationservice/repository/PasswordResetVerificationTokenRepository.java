package com.ericsson.registrationservice.repository;

import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

/**
 * Repository used to manage database-store for password reset verification token entities.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface PasswordResetVerificationTokenRepository
    extends CrudRepository<PasswordResetVerificationToken, Long> {

  @Nullable
  PasswordResetVerificationToken findByToken(final String tokenValue);

}
