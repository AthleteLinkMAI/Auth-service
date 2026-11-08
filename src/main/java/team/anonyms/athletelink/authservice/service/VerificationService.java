package team.anonyms.athletelink.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.dto.service.kafka.NotificationMessage;
import team.anonyms.athletelink.authservice.dto.service.kafka.enums.NotificationType;
import team.anonyms.athletelink.authservice.dto.service.verification.EmailVerificationServiceDto;
import team.anonyms.athletelink.authservice.dto.service.verification.PasswordResetRequestServiceDto;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import team.anonyms.athletelink.authservice.entity.code.EmailCode;
import team.anonyms.athletelink.authservice.entity.code.PasswordResetCode;
import team.anonyms.athletelink.authservice.exception.EntityAlreadyExistsException;
import team.anonyms.athletelink.authservice.repository.EmailVerificationCodeRepository;
import team.anonyms.athletelink.authservice.repository.PasswordResetCodeRepository;
import team.anonyms.athletelink.authservice.repository.UserCredentialsRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * {@code VerificationService} is dedicated to handle all verification related logic.
 */
@Service
@RequiredArgsConstructor
public class VerificationService {
    private static final String CODE_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String PASSWORD_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-_";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration CODE_EXPIRATION = Duration.of(15, ChronoUnit.MINUTES);

    private final KafkaProducerService kafkaProducerService;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final UserCredentialsRepository userCredentialsRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void generateAndSendEmailCode(UUID userId) {
        UserCredentials userCredentials = userCredentialsRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found; userId=" + userId));

        if (userCredentials.getIsVerified()) {
            throw new IllegalArgumentException("User is already verified; userId=" + userId);
        }

        if (emailVerificationCodeRepository.findByUserId(userId).isPresent()) {
            throw new EntityAlreadyExistsException("Email verification code already exists; userId=" + userId);
        }

        EmailCode emailCode = EmailCode.builder()
                .code(generateRawCode())
                .expiration(Instant.now().plus(CODE_EXPIRATION))
                .userCredentials(userCredentials)
                .build();

        emailVerificationCodeRepository.save(emailCode);
        kafkaProducerService.sendNotificationMessage(
                new NotificationMessage(
                        NotificationType.EMAIL,
                        userCredentials.getEmail(),
                        "Email verification code",
                        "Your email verification code is:\n" + emailCode.getCode()
                ),
                userId
        );
    }

    @Transactional
    public void verifyEmail(EmailVerificationServiceDto emailVerificationServiceDto) {
        UserCredentials userCredentials = userCredentialsRepository.findById(emailVerificationServiceDto.userId())
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

        userCredentials.setIsVerified(true);
        userCredentialsRepository.save(userCredentials);
        emailVerificationCodeRepository.delete(emailCode);
    }

    @Transactional
    public void generateAndSendPasswordResetCode(String email) {
        UserCredentials userCredentials = userCredentialsRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found; email=" + email));

        if (passwordResetCodeRepository.findByUserId(userCredentials.getUserId()).isPresent()) {
            throw new EntityAlreadyExistsException(
                    "Password reset code already exists; userId=" + userCredentials.getUserId()
            );
        }

        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .code(generateRawCode())
                .expiration(Instant.now().plus(CODE_EXPIRATION))
                .userCredentials(userCredentials)
                .build();

        passwordResetCodeRepository.save(passwordResetCode);
        kafkaProducerService.sendNotificationMessage(
                new NotificationMessage(
                        NotificationType.EMAIL,
                        userCredentials.getEmail(),
                        "Password reset code",
                        "Your password reset code is:\n" + passwordResetCode.getCode()
                ),
                userCredentials.getUserId()
        );
    }

    private String generatePassword() {
        final int passwordLength = 16;

        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            password.append(PASSWORD_SYMBOLS.charAt(RANDOM.nextInt(PASSWORD_SYMBOLS.length())));
        }

        return password.toString();
    }

    @Transactional
    public void validatePasswordResetCodeAndResetPassword(
            PasswordResetRequestServiceDto passwordResetRequestServiceDto
    ) {
        UserCredentials userCredentials = userCredentialsRepository.findByEmail(passwordResetRequestServiceDto.email()).orElseThrow(
                () -> new EntityNotFoundException("User not found; email={}" + passwordResetRequestServiceDto.email()));

        PasswordResetCode passwordResetCode = passwordResetCodeRepository.findByUserId(userCredentials.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No verification code found for user; userId=" + userCredentials.getUserId()
                ));

        if (passwordResetCode.getExpiration().isBefore(Instant.now())) {
            passwordResetCodeRepository.delete(passwordResetCode);
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (!passwordResetCode.getCode().equals(passwordResetRequestServiceDto.code())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        passwordResetCodeRepository.delete(passwordResetCode);

        String newPassword = generatePassword();

        userCredentials.setPassword(passwordEncoder.encode(newPassword));
        userCredentialsRepository.save(userCredentials);

        kafkaProducerService.sendNotificationMessage(
                new NotificationMessage(
                        NotificationType.EMAIL,
                        userCredentials.getEmail(),
                        "New password",
                        "Your new password is:\n" + newPassword
                ),
                userCredentials.getUserId()
        );
    }

    private String generateRawCode() {
        final int codeLength = 6;

        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(CODE_SYMBOLS.charAt(RANDOM.nextInt(CODE_SYMBOLS.length())));
        }

        return code.toString();
    }
}