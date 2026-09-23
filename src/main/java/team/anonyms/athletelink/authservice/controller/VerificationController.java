package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.anonyms.athletelink.authservice.dto.controller.verification.EmailVerificationControllerDto;
import team.anonyms.athletelink.authservice.dto.controller.verification.PasswordResetRequestControllerDto;
import team.anonyms.athletelink.authservice.mapper.VerificationMapper;
import team.anonyms.athletelink.authservice.service.VerificationService;

import java.util.UUID;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    private final VerificationService verificationService;
    private final VerificationMapper verificationMapper;

    @PostMapping("/send-email-verification-code")
    public ResponseEntity<Void> sendEmailVerificationCode(@AuthenticationPrincipal UUID userId) {
        logger.info("Called send-email-code; userId={}", userId);

        verificationService.generateAndSendEmailCode(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Boolean> verifyEmail(
            @RequestBody String emailVerificationCode,
            @AuthenticationPrincipal UUID userId
    ) {
        logger.info("Called email; userId={}", userId);

        EmailVerificationControllerDto controllerDto = new EmailVerificationControllerDto(
                emailVerificationCode,
                userId
        );

        verificationService.verifyEmail(verificationMapper.emailVerificationControllerToServiceDto(controllerDto));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-password-reset-code")
    public ResponseEntity<Void> sendPasswordResetCode(@RequestBody @Email String email) {
        logger.info("Called forgot-password; email={}", email);

        verificationService.generateAndSendPasswordResetCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-password-reset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid PasswordResetRequestControllerDto passwordResetRequestControllerDto
    ) {
        logger.info("Called password-reset; email={}", passwordResetRequestControllerDto.email());

        verificationService.validatePasswordResetCodeAndResetPassword(
                verificationMapper.passwordResetRequestServiceToControllerDto(passwordResetRequestControllerDto)
        );

        return ResponseEntity.ok().build();
    }
}
