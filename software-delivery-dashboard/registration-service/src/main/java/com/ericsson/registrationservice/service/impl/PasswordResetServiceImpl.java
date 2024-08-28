package com.ericsson.registrationservice.service.impl;

import com.ericsson.registrationservice.dto.PasswordResetDto;
import com.ericsson.registrationservice.model.auth.ApplicationUser;
import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import com.ericsson.registrationservice.model.auth.factory.PasswordResetFactory;
import com.ericsson.registrationservice.repository.ApplicationUserRepository;
import com.ericsson.registrationservice.repository.PasswordResetVerificationTokenRepository;
import com.ericsson.registrationservice.service.MailService;
import com.ericsson.registrationservice.service.PasswordResetService;
import com.ericsson.registrationservice.service.RegistrationService;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service used to handle resetting user passwords.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@RequiredArgsConstructor
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

  static final String PASSWORD_RESET_VERIFICATION_EMAIL_SUBJECT = "Password Reset";

  private final ApplicationUserRepository applicationUserRepository;

  private final PasswordResetVerificationTokenRepository passwordResetVerificationTokenRepository;

  private final MailService mailService;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public PasswordResetStatus requestPasswordReset(final String email) {
    final ApplicationUser applicationUser = this.applicationUserRepository.findByEmail(email);
    if (applicationUser == null) {
      return PasswordResetStatus.FAILURE;
    }

    final PasswordResetVerificationToken passwordResetVerificationToken
        = PasswordResetFactory.generateValidPasswordToken();
    applicationUser.setPasswordResetVerificationToken(
        this.passwordResetVerificationTokenRepository.save(passwordResetVerificationToken)
    );
    this.applicationUserRepository.save(applicationUser);

    this.mailService.sendMail(
        applicationUser.getEmail(),
        PASSWORD_RESET_VERIFICATION_EMAIL_SUBJECT,
        generatePasswordResetVerificationEmailText(applicationUser)
    );

    return PasswordResetStatus.AWAITING_EMAIL_VERIFICATION;
  }

  private String generatePasswordResetVerificationEmailText(final ApplicationUser applicationUser) {
    return String.format("Hello %s %s\n"
            + "Please click the following link to reset your password: "
            + "%s/password/reset/confirm?token=%s",
        applicationUser.getFirstName(), applicationUser.getLastName(),
        RegistrationService.REGISTRATION_SERVICE_BASE_URL,
        applicationUser.getPasswordResetVerificationToken().getToken());
  }

  @Transactional
  @Override
  public PasswordResetStatus confirmPasswordReset(
      final String passwordResetTokenValue, final PasswordResetDto passwordResetDto) {

    if (!passwordResetDto.getPassword().equals(passwordResetDto.getConfirmPassword())) {
      return PasswordResetStatus.PASSWORDS_DO_NOT_MATCH;
    }

    final PasswordResetVerificationToken passwordResetVerificationToken
        = this.passwordResetVerificationTokenRepository.findByToken(passwordResetTokenValue);

    if (passwordResetVerificationToken == null) {
      return PasswordResetStatus.FAILURE;
    }

    final ApplicationUser applicationUser
        = this.applicationUserRepository
        .findByPasswordResetVerificationToken(passwordResetVerificationToken);

    if (applicationUser == null) {
      return PasswordResetStatus.FAILURE;
    }

    if (ZonedDateTime.now().isBefore(passwordResetVerificationToken.getExpiryDateTime())) {
      applicationUser.setPassword(this.passwordEncoder.encode(passwordResetDto.getPassword()));
      this.applicationUserRepository.save(applicationUser);
      passwordResetVerificationToken.setExpiryDateTime(ZonedDateTime.now());
      this.passwordResetVerificationTokenRepository.save(passwordResetVerificationToken);
      return PasswordResetStatus.SUCCESS;
    } else {
      return PasswordResetStatus.EMAIL_VERIFICATION_EXPIRED;
    }
  }

}
