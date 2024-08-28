package com.ericsson.registrationservice.controller;

import com.ericsson.registrationservice.dto.PasswordResetDto;
import com.ericsson.registrationservice.dto.PasswordResetResponseDto;
import com.ericsson.registrationservice.service.PasswordResetService;
import com.ericsson.registrationservice.service.PasswordResetService.PasswordResetStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used to handle resets of passwords.
 *
 * @author Jonathan Lee <jonathan.lee@ericsson.com>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(PasswordResetController.PASSWORD_RESET_CONTROLLER_BASE_URI)
public class PasswordResetController {

  public static final String PASSWORD_RESET_CONTROLLER_BASE_URI = "/password/reset";

  private final PasswordResetService passwordResetService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<PasswordResetResponseDto> postPasswordReset(
      @RequestParam final String email) {
    final PasswordResetResponseDto passwordResetResponseDto = new PasswordResetResponseDto(
        this.passwordResetService.requestPasswordReset(email));

    return (passwordResetResponseDto.getPasswordResetStatus()
        == PasswordResetStatus.AWAITING_EMAIL_VERIFICATION)
        ? ResponseEntity.ok(passwordResetResponseDto)
        : ResponseEntity.internalServerError().body(passwordResetResponseDto);
  }

  @PostMapping("/confirm")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<PasswordResetResponseDto> postPasswordResetConfirm(
      @RequestParam final String token,
      @RequestBody final PasswordResetDto passwordResetDto) {

    final PasswordResetResponseDto passwordResetResponseDto = new PasswordResetResponseDto(
        this.passwordResetService.confirmPasswordReset(token, passwordResetDto));

    switch (passwordResetResponseDto.getPasswordResetStatus()) {
      case SUCCESS:
        return ResponseEntity.ok(passwordResetResponseDto);
      case EMAIL_VERIFICATION_EXPIRED:
      case PASSWORDS_DO_NOT_MATCH:
        return ResponseEntity.badRequest().body(passwordResetResponseDto);
      default:
        return ResponseEntity.internalServerError().body(passwordResetResponseDto);
    }
  }

}
