package com.ericsson.registrationservice.service.impl;

import com.ericsson.registrationservice.dto.RegistrationDto;
import com.ericsson.registrationservice.model.auth.ApplicationUser;
import com.ericsson.registrationservice.model.auth.RegistrationVerificationToken;
import com.ericsson.registrationservice.model.auth.factory.ApplicationUserFactory;
import com.ericsson.registrationservice.repository.ApplicationUserRepository;
import com.ericsson.registrationservice.repository.PasswordResetVerificationTokenRepository;
import com.ericsson.registrationservice.repository.RegistrationVerificationTokenRepository;
import com.ericsson.registrationservice.service.MailService;
import com.ericsson.registrationservice.service.RegistrationService;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service used to handle registering new users.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

  static final String REGISTRATION_VERIFICATION_EMAIL_SUBJECT = "Registration Confirmation";

  private static final String VALID_EMAIL_DOMAIN = "@ericsson.com";

  private final ApplicationUserRepository applicationUserRepository;

  private final PasswordEncoder passwordEncoder;

  private final RegistrationVerificationTokenRepository registrationVerificationTokenRepository;

  private final PasswordResetVerificationTokenRepository passwordResetVerificationTokenRepository;

  private final MailService mailService;

  @Override
  public RegistrationStatus registerUser(final RegistrationDto registrationDto) {
    if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
      return RegistrationStatus.PASSWORDS_DO_NOT_MATCH;
    }
    if (!registrationDto.getEmail().endsWith(VALID_EMAIL_DOMAIN)) {
      return RegistrationStatus.INVALID_EMAIL_DOMAIN;
    }

    final ApplicationUser storedApplicationUser
        = this.applicationUserRepository.findByEmail(registrationDto.getEmail());

    if (storedApplicationUser != null) {
      if (storedApplicationUser.isEnabled()) {
        return RegistrationStatus.USER_ALREADY_EXISTS;
      } else {
        this.applicationUserRepository.delete(storedApplicationUser);
      }
    }

    final ApplicationUser newApplicationUser = ApplicationUserFactory.generateApplicationUser(
        registrationDto.getEmail(),
        registrationDto.getFirstName(),
        registrationDto.getLastName(),
        this.passwordEncoder.encode(registrationDto.getPassword())
    );
    newApplicationUser.setRegistrationVerificationToken(
        this.registrationVerificationTokenRepository.save(
            newApplicationUser.getRegistrationVerificationToken())
    );
    newApplicationUser.setPasswordResetVerificationToken(
        this.passwordResetVerificationTokenRepository.save(
            newApplicationUser.getPasswordResetVerificationToken())
    );
    this.applicationUserRepository.save(newApplicationUser);

    this.mailService.sendMail(
        registrationDto.getEmail(),
        REGISTRATION_VERIFICATION_EMAIL_SUBJECT,
        generateRegistrationVerificationEmailText(newApplicationUser)
    );

    return RegistrationStatus.AWAITING_EMAIL_VERIFICATION;
  }

  private String generateRegistrationVerificationEmailText(final ApplicationUser applicationUser) {
    return String.format("Greetings %s %s\n"
            + "Please click the following link to verify your account: "
            + "%s/register/confirm?token=%s",
        applicationUser.getFirstName(), applicationUser.getLastName(),
        RegistrationService.REGISTRATION_SERVICE_BASE_URL,
        applicationUser.getRegistrationVerificationToken().getToken());
  }

  @Transactional
  @Override
  public RegistrationStatus confirmUserRegistration(
      final String registrationVerificationTokenValue) {

    final RegistrationVerificationToken registrationVerificationToken
        = this.registrationVerificationTokenRepository
        .findByToken(registrationVerificationTokenValue);

    if (registrationVerificationToken == null) {
      return RegistrationStatus.FAILURE;
    }

    final ApplicationUser applicationUser = this.applicationUserRepository
        .findByRegistrationVerificationToken(registrationVerificationToken);

    if (applicationUser == null) {
      return RegistrationStatus.FAILURE;
    }

    if (ZonedDateTime.now().isBefore(registrationVerificationToken.getExpiryDateTime())) {
      applicationUser.setEnabled(true);
      this.applicationUserRepository.save(applicationUser);
      return RegistrationStatus.SUCCESS;
    } else {
      this.applicationUserRepository.delete(applicationUser);
      return RegistrationStatus.EMAIL_VERIFICATION_EXPIRED;
    }
  }

}
