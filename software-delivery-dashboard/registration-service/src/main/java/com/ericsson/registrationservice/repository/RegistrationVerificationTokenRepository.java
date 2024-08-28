package com.ericsson.registrationservice.repository;

import com.ericsson.registrationservice.model.auth.RegistrationVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

/**
 * Repository used to manage database-store for registration verification token entities.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface RegistrationVerificationTokenRepository
    extends CrudRepository<RegistrationVerificationToken, Long> {

  @Nullable
  RegistrationVerificationToken findByToken(final String token);

}
