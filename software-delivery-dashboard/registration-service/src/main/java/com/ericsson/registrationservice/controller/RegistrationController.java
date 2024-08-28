package com.ericsson.registrationservice.controller;

import com.ericsson.registrationservice.dto.RegistrationDto;
import com.ericsson.registrationservice.dto.RegistrationResponseDto;
import com.ericsson.registrationservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used to handle the registration of new users.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(RegistrationController.REGISTRATION_CONTROLLER_BASE_URI)
public class RegistrationController {

  static final String REGISTRATION_CONTROLLER_BASE_URI = "/register";

  private final RegistrationService registrationService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<RegistrationResponseDto> postRegister(
      @RequestBody final RegistrationDto registrationDto) {

    final RegistrationResponseDto registrationResponseDto
        = new RegistrationResponseDto(this.registrationService.registerUser(registrationDto));

    switch (registrationResponseDto.getRegistrationStatus()) {
      case AWAITING_EMAIL_VERIFICATION:
        return ResponseEntity.ok(registrationResponseDto);
      case INVALID_EMAIL_DOMAIN:
      case PASSWORDS_DO_NOT_MATCH:
        return ResponseEntity.badRequest().body(registrationResponseDto);
      case USER_ALREADY_EXISTS:
        return new ResponseEntity<>(registrationResponseDto, HttpStatus.CONFLICT);
      default:
        return ResponseEntity.internalServerError().body(registrationResponseDto);
    }
  }

  @GetMapping("/confirm")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<RegistrationResponseDto> getRegisterConfirm(
      @RequestParam("token") final String registrationVerificationTokenValue) {

    final RegistrationResponseDto registrationResponseDto
        = new RegistrationResponseDto(
        this.registrationService.confirmUserRegistration(registrationVerificationTokenValue));

    switch (registrationResponseDto.getRegistrationStatus()) {
      case SUCCESS:
        return ResponseEntity.ok(registrationResponseDto);
      case EMAIL_VERIFICATION_EXPIRED:
        return ResponseEntity.badRequest().body(registrationResponseDto);
      default:
        return ResponseEntity.internalServerError().body(registrationResponseDto);
    }
  }

}
