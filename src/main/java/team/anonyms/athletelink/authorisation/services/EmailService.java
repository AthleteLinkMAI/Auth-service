package team.anonyms.athletelink.authorisation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authorisation.entities.User;
import team.anonyms.athletelink.authorisation.entities.VerificationCode;
import team.anonyms.athletelink.authorisation.enums.VerificationCodeType;
import team.anonyms.athletelink.authorisation.repositories.UserRepository;
import team.anonyms.athletelink.authorisation.repositories.VerificationCodeRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * {@code EmailService} is dedicated to handle sending emails.
 */
// TODO: Sending an email hasn't been realised yet.
@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final int CODE_EXPIRATION_MINUTES = 15; 

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;

    public EmailService(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Transactional
    public void generateAndSendEmailCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getIsVerified()) {
            throw new IllegalArgumentException("User is already verified");
        }

        SecureRandom random = new SecureRandom();
        String code = String.format("%06d", random.nextInt(1000000));
        Instant expiration = Instant.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        VerificationCode verificationCode = verificationCodeRepository.findByUserId(user.getId())
                .orElse(VerificationCode.builder().build());

        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setType(VerificationCodeType.EMAIL);
        verificationCode.setExpiration(expiration);

        verificationCodeRepository.save(verificationCode);
        
        logger.info(" EMAIL VERIFICATION CODE FOR {}: {}", email, code);
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        VerificationCode verificationCode = verificationCodeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No verification code found for user"));

        if (verificationCode.getType() != VerificationCodeType.EMAIL) {
            throw new IllegalArgumentException("Invalid code type");
        }

        if (verificationCode.getExpiration().isBefore(Instant.now())) {
            verificationCodeRepository.delete(verificationCode);
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (!verificationCode.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        user.setIsVerified(true);
        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);
    }

    @Transactional
    public void generateAndSendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SecureRandom random = new SecureRandom();
        String code = String.format("%06d", random.nextInt(1000000));
        Instant expiration = Instant.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        VerificationCode verificationCode = verificationCodeRepository.findByUserId(user.getId())
                .orElse(VerificationCode.builder().build());

        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setType(VerificationCodeType.PASSWORD); 
        verificationCode.setExpiration(expiration);

        verificationCodeRepository.save(verificationCode);

        logger.info(" PASSWORD RESET CODE FOR {}: {}", email, code);
    }

    @Transactional
    public void validateAndConsumePasswordResetCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        VerificationCode verificationCode = verificationCodeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No verification code found for user"));

        if (verificationCode.getType() != VerificationCodeType.PASSWORD) {
            throw new IllegalArgumentException("Invalid code type. Expected password reset code.");
        }

        if (verificationCode.getExpiration().isBefore(Instant.now())) {
            verificationCodeRepository.delete(verificationCode);
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (!verificationCode.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        verificationCodeRepository.delete(verificationCode);
    }
}