package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.dto.service.verification.EmailVerificationServiceDto;
import team.anonyms.athletelink.authservice.dto.service.verification.PasswordResetRequestServiceDto;
import team.anonyms.athletelink.authservice.entity.User;
import team.anonyms.athletelink.authservice.entity.code.EmailCode;
import team.anonyms.athletelink.authservice.entity.code.PasswordResetCode;
import team.anonyms.athletelink.authservice.exception.CodeAlreadyExistsException;
import team.anonyms.athletelink.authservice.repository.EmailVerificationCodeRepository;
import team.anonyms.athletelink.authservice.repository.PasswordResetCodeRepository;
import team.anonyms.athletelink.authservice.repository.UserRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * {@code VerificationService} is dedicated to handle all verification related logic.
 */
// TODO: add connection with Kafka
@Service
@RequiredArgsConstructor
public class VerificationService {
    // Symbols for generating random codes
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration CODE_EXPIRATION = Duration.of(15, ChronoUnit.MINUTES);

    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void generateAndSendEmailCode(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found; userId=" + userId));

        if (user.getIsVerified()) {
            throw new IllegalArgumentException("User is already verified; userId=" + userId);
        }

        if (emailVerificationCodeRepository.findByUserId(userId).isPresent()) {
            throw new CodeAlreadyExistsException("Email verification code already exists; userId=" + userId);
        }

        EmailCode emailCode = EmailCode.builder()
                .code(generateRawCode())
                .expiration(Instant.now().plus(CODE_EXPIRATION))
                .user(user)
                .build();

        emailVerificationCodeRepository.save(emailCode);
        sendToKafka(emailCode);
    }

    @Transactional
    public void verifyEmail(EmailVerificationServiceDto emailVerificationServiceDto) {
        User user = userRepository.findById(emailVerificationServiceDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found; userId=" + emailVerificationServiceDto.userId()));

        EmailCode emailCode = emailVerificationCodeRepository.findByUserId(emailVerificationServiceDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("No verification code found for user; userId=" + emailVerificationServiceDto.userId()));

        if (emailCode.getExpiration().isBefore(Instant.now())) {
            emailVerificationCodeRepository.delete(emailCode);
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (!emailCode.getCode().equals(emailVerificationServiceDto.emailVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        user.setIsVerified(true);
        userRepository.save(user);
        emailVerificationCodeRepository.delete(emailCode);
    }

    @Transactional
    public void generateAndSendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found; email=" + email));

        if (passwordResetCodeRepository.findByUserId(user.getId()).isPresent()) {
            throw new CodeAlreadyExistsException("Password reset code already exists; userId=" + user.getId());
        }

        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .code(generateRawCode())
                .expiration(Instant.now().plus(CODE_EXPIRATION))
                .user(user)
                .build();

        passwordResetCodeRepository.save(passwordResetCode);
        sendToKafka(passwordResetCode);
    }

    @Transactional
    public void validatePasswordResetCodeAndResetPassword(
            PasswordResetRequestServiceDto passwordResetRequestServiceDto
    ) {
        User user = userRepository.findByEmail(passwordResetRequestServiceDto.email()).orElseThrow(
                () -> new EntityNotFoundException("User not found; email={}" + passwordResetRequestServiceDto.email()));

        PasswordResetCode passwordResetCode = passwordResetCodeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No verification code found for user; userId=" + user.getId()));

        if (passwordResetCode.getExpiration().isBefore(Instant.now())) {
            passwordResetCodeRepository.delete(passwordResetCode);
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (!passwordResetCode.getCode().equals(passwordResetRequestServiceDto.code())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        passwordResetCodeRepository.delete(passwordResetCode);
        // Notification service will generate new password and send it to a user's email
        sendToKafka("Generate new password for email=" + passwordResetRequestServiceDto.email());
    }

    private String generateRawCode() {
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        }

        return code.toString();
    }

    // Plug before adding connection to Kafka
    private void sendToKafka(Object object) {

    }
}