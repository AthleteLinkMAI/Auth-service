package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.anonyms.athletelink.authservice.dto.controller.verification.EmailVerificationControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.verification.PasswordResetRequestControllerDto;
import team.anonyms.athletelink.authservice.mapper.VerificationMapper;
import team.anonyms.athletelink.authservice.service.VerificationService;

import java.util.UUID;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {
    private final VerificationService verificationService;
    private final VerificationMapper verificationMapper;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/send-email-verification-code")
    public void sendEmailVerificationCode(@RequestHeader("X-User-Id") UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/send-email-verification-code/{userId}")
                .addKeyValue("userId", userId)
                .log();

        verificationService.generateAndSendEmailCode(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/email")
    public void verifyEmail(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody String emailVerificationCode
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/verify-email/{userId}")
                .addKeyValue("userId", userId)
                .log();

        EmailVerificationControllerDto controllerDto = new EmailVerificationControllerDto(
                emailVerificationCode,
                userId
        );

        verificationService.verifyEmail(verificationMapper.emailVerificationControllerToServiceDto(controllerDto));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send-password-reset-code")
    public void sendPasswordResetCode(@RequestBody @Email String email) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/send-password-reset-code")
                .log();

        verificationService.generateAndSendPasswordResetCode(email);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/password-reset")
    public void resetPassword(
            @RequestBody @Valid PasswordResetRequestControllerDto passwordResetRequestControllerDto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/verify-password-reset")
                .log();

        verificationService.validatePasswordResetCodeAndResetPassword(
                verificationMapper.passwordResetRequestControllerToServiceDto(passwordResetRequestControllerDto)
        );
    }
}
