package team.anonyms.athletelink.authservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.anonyms.athletelink.authservice.dto.controller.auth.VerifyCodeRequestControllerDto;
import team.anonyms.athletelink.authservice.service.VerificationService;

import java.util.UUID;

@RestController
@RequestMapping("/verify")
public class VerificationController {
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<Void> sendVerificationCode(@AuthenticationPrincipal UUID userId) {
        logger.info("Called send-email-code; userId={}", userId);

        verificationService.generateAndSendEmailCode(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email")
    public ResponseEntity<Boolean> verifyEmail(
            @RequestBody String emailVerificationCode,
            @AuthenticationPrincipal UUID userId
    ) {
        logger.info("Called email; userId={}", userId);

        verificationService.verifyEmail(userId, emailVerificationCode);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Email String email) {
        logger.info("Called forgot-password; email={}", email);

        verificationService.generateAndSendPasswordResetCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> verifyPasswordReset(
            @RequestBody @Valid VerifyCodeRequestControllerDto verifyCodeRequestControllerDto
    ) {
        logger.info("Called password-reset; email={}", verifyCodeRequestControllerDto.email());

        verificationService.validateAndConsumePasswordResetCode(verifyCodeRequestControllerDto);
        return ResponseEntity.ok().build();
    }
}
