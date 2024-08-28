package com.ericsson.registrationservice.dto;

import com.ericsson.registrationservice.service.PasswordResetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordResetResponseDtoTest {

    private final PasswordResetResponseDto passwordResetResponseDto = new PasswordResetResponseDto(
        PasswordResetService.PasswordResetStatus.FAILURE);
    private final PasswordResetService.PasswordResetStatus passwordResetStatus =
        PasswordResetService.PasswordResetStatus.FAILURE;

    @Test
    void passwordResetStatusGetter() {
        Assertions.assertEquals(passwordResetStatus,
            passwordResetResponseDto.getPasswordResetStatus());
    }

}
