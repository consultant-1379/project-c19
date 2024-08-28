package com.ericsson.registrationservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.ericsson.registrationservice.dto.RegistrationDto;
import com.ericsson.registrationservice.model.auth.ApplicationUser;
import com.ericsson.registrationservice.model.auth.PasswordResetVerificationToken;
import com.ericsson.registrationservice.model.auth.RegistrationVerificationToken;
import com.ericsson.registrationservice.model.auth.factory.ApplicationUserFactory;
import com.ericsson.registrationservice.model.auth.factory.PasswordResetFactory;
import com.ericsson.registrationservice.model.auth.factory.RegistrationFactory;
import com.ericsson.registrationservice.repository.ApplicationUserRepository;
import com.ericsson.registrationservice.repository.PasswordResetVerificationTokenRepository;
import com.ericsson.registrationservice.repository.RegistrationVerificationTokenRepository;
import com.ericsson.registrationservice.service.MailService;
import com.ericsson.registrationservice.service.RegistrationService.RegistrationStatus;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class RegistrationServiceImplTest {

  private static final String VALID_EMAIL = "johndoe@ericsson.com";
  private static final String INVALID_EMAIL = "johndoe@mail.com";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";
  private static final String PASSWORD = "password";
  private static final String ALTERNATIVE_PASSWORD = "password1";
  private static final String TOKEN = UUID.randomUUID().toString();

  private ApplicationUserRepository applicationUserRepository;

  private PasswordEncoder passwordEncoder;

  private RegistrationVerificationTokenRepository registrationVerificationTokenRepository;

  private PasswordResetVerificationTokenRepository passwordResetVerificationTokenRepository;

  private MailService mailService;

  private RegistrationServiceImpl registrationService;

  @BeforeEach
  void beforeEach() {
    this.applicationUserRepository = Mockito.mock(ApplicationUserRepository.class);
    this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
    this.registrationVerificationTokenRepository
        = Mockito.mock(RegistrationVerificationTokenRepository.class);
    this.passwordResetVerificationTokenRepository
        = Mockito.mock(PasswordResetVerificationTokenRepository.class);
    this.mailService = Mockito.mock(MailService.class);
    this.registrationService = new RegistrationServiceImpl(
        this.applicationUserRepository,
        this.passwordEncoder,
        this.registrationVerificationTokenRepository,
        this.passwordResetVerificationTokenRepository,
        this.mailService
    );
  }

  @Test
  void whenRegistrationServiceImpl_thenNotNull() {
    Assertions.assertNotNull(this.registrationService);
  }

  @Test
  void whenRegisterUser_mismatchedPasswords_thenReturnStatusPasswordsDoNotMatch() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(ALTERNATIVE_PASSWORD)
        .build();

    final RegistrationStatus registrationStatus
        = this.registrationService.registerUser(registrationDto);

    Assertions.assertEquals(RegistrationStatus.PASSWORDS_DO_NOT_MATCH, registrationStatus);
  }

  @Test
  void whenRegisterUser_invalidEmailDomain_thenReturnStatusInvalidEmailDomain() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(INVALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final RegistrationStatus registrationStatus
        = this.registrationService.registerUser(registrationDto);

    Assertions.assertEquals(RegistrationStatus.INVALID_EMAIL_DOMAIN, registrationStatus);
  }

  @Test
  void whenRegisterUser_enabledUserExists_thenReturnStatusUserAlreadyExists() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .isEnabled(true)
        .build();

    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(applicationUser);

    final RegistrationStatus registrationStatus
        = this.registrationService.registerUser(registrationDto);

    Assertions.assertEquals(RegistrationStatus.USER_ALREADY_EXISTS, registrationStatus);
  }

  @Test
  void whenRegisterUser_disabledUserExists_thenDeleteDisabledUser() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .isEnabled(false)
        .build();
    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(applicationUser);

    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(
            this.registrationVerificationTokenRepository.save(any(RegistrationVerificationToken.class)))
        .thenReturn(registrationVerificationToken);

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateExpiredToken();
    Mockito.when(
            this.passwordResetVerificationTokenRepository
                .save(any(PasswordResetVerificationToken.class)))
        .thenReturn(passwordResetVerificationToken);

    this.registrationService.registerUser(registrationDto);

    Mockito.verify(this.applicationUserRepository, Mockito.atLeastOnce())
        .delete(applicationUser);
  }

  @Test
  void whenRegisterUser_validRegistrationNoUserExist_thenPasswordEncode() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(null);

    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(
            this.registrationVerificationTokenRepository.save(any(RegistrationVerificationToken.class)))
        .thenReturn(registrationVerificationToken);

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateExpiredToken();
    Mockito.when(
            this.passwordResetVerificationTokenRepository
                .save(any(PasswordResetVerificationToken.class)))
        .thenReturn(passwordResetVerificationToken);

    this.registrationService.registerUser(registrationDto);

    Mockito.verify(this.passwordEncoder, Mockito.atLeastOnce())
        .encode(PASSWORD);
  }

  @Test
  void whenRegisterUser_validRegistrationNoUserExist_thenSendMail() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(null);

    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(
            this.registrationVerificationTokenRepository.save(any(RegistrationVerificationToken.class)))
        .thenReturn(registrationVerificationToken);

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateExpiredToken();
    Mockito.when(
            this.passwordResetVerificationTokenRepository
                .save(any(PasswordResetVerificationToken.class)))
        .thenReturn(passwordResetVerificationToken);

    this.registrationService.registerUser(registrationDto);

    Mockito.verify(this.mailService, Mockito.atLeastOnce())
        .sendMail(registrationDto.getEmail(),
            RegistrationServiceImpl.REGISTRATION_VERIFICATION_EMAIL_SUBJECT,
            String.format("Greetings %s %s\n"
                    + "Please click the following link to verify your account: "
                    + "%s/register/confirm?token=%s", registrationDto.getFirstName(),
                registrationDto.getLastName(),
                RegistrationServiceImpl.REGISTRATION_SERVICE_BASE_URL,
                registrationVerificationToken.getToken()));
  }

  @Test
  void whenRegisterUser_validRegistrationNoUserExist_thenReturnStatusAwaitingEmailVerification() {
    final RegistrationDto registrationDto = RegistrationDto
        .builder()
        .email(VALID_EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    Mockito.when(this.applicationUserRepository.findByEmail(anyString()))
        .thenReturn(null);

    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(
            this.registrationVerificationTokenRepository.save(any(RegistrationVerificationToken.class)))
        .thenReturn(registrationVerificationToken);

    final PasswordResetVerificationToken passwordResetVerificationToken = PasswordResetFactory.generateExpiredToken();
    Mockito.when(
            this.passwordResetVerificationTokenRepository
                .save(any(PasswordResetVerificationToken.class)))
        .thenReturn(passwordResetVerificationToken);

    Assertions.assertEquals(RegistrationStatus.AWAITING_EMAIL_VERIFICATION,
        this.registrationService.registerUser(registrationDto));
  }

  @Test
  void whenRegisterConfirm_validUserToken_thenRegistrationStatusSuccess() {
    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(registrationVerificationToken);

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .registrationVerificationToken(registrationVerificationToken)
        .build();
    Mockito.when(this.applicationUserRepository
            .findByRegistrationVerificationToken(any(RegistrationVerificationToken.class)))
        .thenReturn(applicationUser);

    Assertions.assertEquals(RegistrationStatus.SUCCESS,
        this.registrationService.confirmUserRegistration(TOKEN));
  }

  @Test
  void whenRegisterConfirm_invalidUserToken_thenRegistrationStatusFailure() {
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(null);

    Assertions.assertEquals(RegistrationStatus.FAILURE,
        this.registrationService.confirmUserRegistration(TOKEN));
  }

  @Test
  void whenRegisterConfirm_invalidUser_thenRegistrationStatusFailure() {
    final ApplicationUser applicationUser = ApplicationUserFactory
        .generateApplicationUser(VALID_EMAIL, FIRST_NAME, LAST_NAME, PASSWORD);
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(applicationUser.getRegistrationVerificationToken());

    Mockito.when(this.applicationUserRepository
            .findByRegistrationVerificationToken(any(RegistrationVerificationToken.class)))
        .thenReturn(null);

    Assertions.assertEquals(RegistrationStatus.FAILURE,
        this.registrationService.confirmUserRegistration(TOKEN));
  }

  @Test
  void whenRegisterConfirm_validUser_thenApplicationUserEnabled() {
    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(registrationVerificationToken);

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .registrationVerificationToken(registrationVerificationToken)
        .build();
    Mockito.when(this.applicationUserRepository
            .findByRegistrationVerificationToken(any(RegistrationVerificationToken.class)))
        .thenReturn(applicationUser);

    this.registrationService.confirmUserRegistration(registrationVerificationToken.getToken());

    Assertions.assertTrue(applicationUser.isEnabled());
  }

  @Test
  void whenRegisterConfirm_validUser_thenApplicationUserRepositorySave() {
    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(registrationVerificationToken);

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .registrationVerificationToken(registrationVerificationToken)
        .build();
    Mockito.when(this.applicationUserRepository
            .findByRegistrationVerificationToken(any(RegistrationVerificationToken.class)))
        .thenReturn(applicationUser);

    this.registrationService.confirmUserRegistration(registrationVerificationToken.getToken());

    Mockito.verify(this.applicationUserRepository, Mockito.atLeastOnce())
        .save(applicationUser);
  }

  @Test
  void whenRegisterConfirm_expiredToken_thenApplicationUserRepositoryDelete() {
    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    registrationVerificationToken.setExpiryDateTime(ZonedDateTime.now());
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(registrationVerificationToken);

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .registrationVerificationToken(registrationVerificationToken)
        .build();
    Mockito.when(this.applicationUserRepository
            .findByRegistrationVerificationToken(any(RegistrationVerificationToken.class)))
        .thenReturn(applicationUser);

    this.registrationService.confirmUserRegistration(registrationVerificationToken.getToken());

    Mockito.verify(this.applicationUserRepository, Mockito.atLeastOnce())
        .delete(applicationUser);
  }

  @Test
  void whenRegisterConfirm_expiredToken_thenRegistrationStatusEmailVerificationExpired() {
    final RegistrationVerificationToken registrationVerificationToken = RegistrationFactory.generateRegistrationToken();
    registrationVerificationToken.setExpiryDateTime(ZonedDateTime.now());
    Mockito.when(this.registrationVerificationTokenRepository.findByToken(anyString()))
        .thenReturn(registrationVerificationToken);

    final ApplicationUser applicationUser = ApplicationUser
        .builder()
        .registrationVerificationToken(registrationVerificationToken)
        .build();
    Mockito.when(this.applicationUserRepository
            .findByRegistrationVerificationToken(any(RegistrationVerificationToken.class)))
        .thenReturn(applicationUser);

    Assertions.assertEquals(RegistrationStatus.EMAIL_VERIFICATION_EXPIRED,
        this.registrationService.confirmUserRegistration(registrationVerificationToken.getToken()));
  }

}
