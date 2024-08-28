package com.ericsson.registrationservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.ericsson.registrationservice.dto.PasswordResetDto;
import com.ericsson.registrationservice.model.auth.ApplicationUser;
import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import com.ericsson.registrationservice.model.auth.factory.ApplicationUserFactory;
import com.ericsson.registrationservice.model.auth.factory.PasswordResetFactory;
import com.ericsson.registrationservice.repository.ApplicationUserRepository;
import com.ericsson.registrationservice.repository.PasswordResetVerificationTokenRepository;
import com.ericsson.registrationservice.service.MailService;
import com.ericsson.registrationservice.service.PasswordResetService.PasswordResetStatus;
import com.ericsson.registrationservice.service.RegistrationService;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordResetServiceImplTest {

  private static final String VALID_EMAIL = "johndoe@ericsson.com";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";
  private static final String PASSWORD = "password";
  private static final String ALTERNATIVE_PASSWORD = "password1";
  private static final String TOKEN = UUID.randomUUID().toString();

  private ApplicationUserRepository applicationUserRepository;

  private PasswordResetVerificationTokenRepository passwordResetVerificationTokenRepository;

  private MailService mailService;

  private PasswordEncoder passwordEncoder;

  private PasswordResetServiceImpl passwordResetService;

  @BeforeEach
  void beforeEach() {
    this.applicationUserRepository = Mockito.mock(ApplicationUserRepository.class);

    this.passwordResetVerificationTokenRepository
        = Mockito.mock(PasswordResetVerificationTokenRepository.class);

    this.mailService = Mockito.mock(MailService.class);

    this.passwordEncoder = Mockito.mock(PasswordEncoder.class);

    this.passwordResetService = new PasswordResetServiceImpl(
        this.applicationUserRepository,
        this.passwordResetVerificationTokenRepository,
        this.mailService,
        this.passwordEncoder
    );
  }

  @Test
  void whenPasswordResetServiceImpl_thenNotNull() {
    Assertions.assertNotNull(this.passwordResetService);
  }

  @Test
  void whenRequestPasswordReset_invalidUser_thenReturnStatusFailure() {
    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(null);

    Assertions.assertEquals(PasswordResetStatus.FAILURE,
        this.passwordResetService.requestPasswordReset(VALID_EMAIL));
  }

  @Test
  void whenRequestPasswordReset_validUser_thenPasswordTokenSaved() {
    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );
    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(applicationUser);

    Mockito.when(this.passwordResetVerificationTokenRepository.save(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(PasswordResetFactory.generateValidPasswordToken());

    this.passwordResetService.requestPasswordReset(VALID_EMAIL);

    Mockito.verify(this.passwordResetVerificationTokenRepository, Mockito.atLeastOnce())
        .save(any(PasswordResetVerificationToken.class));
  }

  @Test
  void whenRequestPasswordReset_validUser_thenApplicationUserSaved() {
    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );
    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(applicationUser);

    Mockito.when(this.passwordResetVerificationTokenRepository.save(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(PasswordResetFactory.generateValidPasswordToken());

    this.passwordResetService.requestPasswordReset(VALID_EMAIL);

    Mockito.verify(this.applicationUserRepository, Mockito.atLeastOnce())
        .save(applicationUser);
  }

  @Test
  void whenRequestPasswordReset_validUser_thenMailSent() {
    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );
    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(applicationUser);

    Mockito.when(this.passwordResetVerificationTokenRepository.save(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(PasswordResetFactory.generateValidPasswordToken());

    this.passwordResetService.requestPasswordReset(VALID_EMAIL);

    Mockito.verify(this.mailService, Mockito.atLeastOnce())
        .sendMail(applicationUser.getEmail(),
            PasswordResetServiceImpl.PASSWORD_RESET_VERIFICATION_EMAIL_SUBJECT,
            String.format("Hello %s %s\n"
                    + "Please click the following link to reset your password: "
                    + "%s/password/reset/confirm?token=%s",
                applicationUser.getFirstName(), applicationUser.getLastName(),
                RegistrationService.REGISTRATION_SERVICE_BASE_URL,
                applicationUser.getPasswordResetVerificationToken().getToken()));
  }

  @Test
  void whenRequestPasswordReset_validUser_thenReturnStatusAwaitingEmailVerification() {
    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );
    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(applicationUser);

    Mockito.when(this.passwordResetVerificationTokenRepository.save(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(PasswordResetFactory.generateValidPasswordToken());

    Assertions.assertEquals(PasswordResetStatus.AWAITING_EMAIL_VERIFICATION,
        this.passwordResetService.requestPasswordReset(VALID_EMAIL));
  }

  @Test
  void whenConfirmPasswordReset_passwordsNotMatch_thenReturnStatusPasswordsDoNotMatch() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(ALTERNATIVE_PASSWORD)
        .build();

    Assertions.assertEquals(PasswordResetStatus.PASSWORDS_DO_NOT_MATCH,
        this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto));
  }

  @Test
  void whenConfirmPasswordReset_validToken_thenPasswordResetVerificationTokenRepositoryFindByToken() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto);

    Mockito.verify(this.passwordResetVerificationTokenRepository, Mockito.atLeastOnce())
        .findByToken(TOKEN);
  }

  @Test
  void whenConfirmPasswordReset_invalidToken_thenReturnStatusFailure() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(null);

    Assertions.assertEquals(PasswordResetStatus.FAILURE,
        this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto));
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenApplicationUserRepositoryFindByToken() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    this.passwordResetService.confirmPasswordReset(
        passwordResetVerificationToken.getToken(), passwordResetDto);

    Mockito.verify(this.applicationUserRepository, Mockito.atLeastOnce())
        .findByPasswordResetVerificationToken(passwordResetVerificationToken);
  }

  @Test
  void whenConfirmPasswordReset_invalidUser_thenReturnStatusFailure() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(PasswordResetFactory.generateValidPasswordToken());

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(null);

    Assertions.assertEquals(PasswordResetStatus.FAILURE,
        this.passwordResetService.confirmPasswordReset(TOKEN, passwordResetDto));
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenPasswordEncoded() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    this.passwordResetService.confirmPasswordReset(
        passwordResetVerificationToken.getToken(), passwordResetDto);

    Mockito.verify(this.passwordEncoder, Mockito.atLeastOnce()).encode(PASSWORD);
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenPasswordUserSetPasswordEncoded() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    Mockito.when(this.passwordEncoder.encode(PASSWORD)).thenReturn(ALTERNATIVE_PASSWORD);

    this.passwordResetService.confirmPasswordReset(
        passwordResetVerificationToken.getToken(), passwordResetDto);

    Assertions.assertEquals(ALTERNATIVE_PASSWORD, applicationUser.getPassword());
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenUserSaved() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    Mockito.when(this.passwordEncoder.encode(PASSWORD)).thenReturn(ALTERNATIVE_PASSWORD);

    this.passwordResetService.confirmPasswordReset(
        passwordResetVerificationToken.getToken(), passwordResetDto);

    Mockito.verify(this.applicationUserRepository, Mockito.atLeastOnce())
        .save(applicationUser);
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenTokenExpired() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    this.passwordResetService.confirmPasswordReset(
        passwordResetVerificationToken.getToken(), passwordResetDto);

    Assertions.assertTrue(
        passwordResetVerificationToken.getExpiryDateTime().isBefore(ZonedDateTime.now())
    );
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenTokenSaved() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    this.passwordResetService.confirmPasswordReset(
        passwordResetVerificationToken.getToken(), passwordResetDto);

    Mockito.verify(this.passwordResetVerificationTokenRepository, Mockito.atLeastOnce())
        .save(passwordResetVerificationToken);
  }

  @Test
  void whenConfirmPasswordReset_validUser_thenReturnStatusSuccess() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateValidPasswordToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    Assertions.assertEquals(PasswordResetStatus.SUCCESS,
        this.passwordResetService
            .confirmPasswordReset(passwordResetVerificationToken.getToken(), passwordResetDto));
  }

  @Test
  void whenConfirmPasswordReset_expiredToken_thenReturnStatusEmailVerificationExpired() {
    final PasswordResetDto passwordResetDto = PasswordResetDto
        .builder()
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateExpiredToken();

    Mockito.when(this.passwordResetVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(passwordResetVerificationToken);

    final ApplicationUser applicationUser = ApplicationUserFactory.generateApplicationUser(
        VALID_EMAIL,
        FIRST_NAME,
        LAST_NAME,
        PASSWORD
    );

    Mockito.when(this.applicationUserRepository.findByPasswordResetVerificationToken(
            any(PasswordResetVerificationToken.class)))
        .thenReturn(applicationUser);

    Assertions.assertEquals(PasswordResetStatus.EMAIL_VERIFICATION_EXPIRED,
        this.passwordResetService
            .confirmPasswordReset(passwordResetVerificationToken.getToken(), passwordResetDto));
  }

}
