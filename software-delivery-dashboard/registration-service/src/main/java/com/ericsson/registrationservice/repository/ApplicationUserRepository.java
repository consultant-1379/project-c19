package com.ericsson.registrationservice.repository;

import com.ericsson.registrationservice.model.auth.ApplicationUser;
import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import com.ericsson.registrationservice.model.auth.RegistrationVerificationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

/**
 * Repository used to manage database-store for application user entities.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Long> {

  @Nullable
  ApplicationUser findByEmail(final String email);

  @Nullable
  ApplicationUser findByRegistrationVerificationToken(
      final RegistrationVerificationToken registrationVerificationToken
  );

  @Nullable
  ApplicationUser findByPasswordResetVerificationToken(
      final PasswordResetVerificationToken passwordResetVerificationToken
  );

}
