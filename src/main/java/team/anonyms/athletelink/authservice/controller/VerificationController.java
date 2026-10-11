package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/send-email-verification-code/{userId}")
    public ResponseEntity<Void> sendEmailVerificationCode(@PathVariable UUID userId) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/send-email-verification-code/{userId}")
                .addKeyValue("userId", userId)
                .log();

        verificationService.generateAndSendEmailCode(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email/{userId}")
    public ResponseEntity<Boolean> verifyEmail(
            @RequestBody String emailVerificationCode,
            @PathVariable UUID userId
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
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-password-reset-code")
    public ResponseEntity<Void> sendPasswordResetCode(@RequestBody @Email String email) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/send-password-reset-code")
                .log();

        verificationService.generateAndSendPasswordResetCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-password-reset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid PasswordResetRequestControllerDto passwordResetRequestControllerDto
    ) {
        log.atInfo()
                .setMessage("API endpoint is called")
                .addKeyValue("API endpoint", "POST /verification/verify-password-reset")
                .log();

        verificationService.validatePasswordResetCodeAndResetPassword(
                verificationMapper.passwordResetRequestControllerToServiceDto(passwordResetRequestControllerDto)
        );

        return ResponseEntity.ok().build();
    }
}
