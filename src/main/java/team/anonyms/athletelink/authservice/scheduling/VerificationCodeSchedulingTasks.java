package team.anonyms.athletelink.authservice.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.repository.EmailVerificationCodeRepository;
import team.anonyms.athletelink.authservice.repository.PasswordResetCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class VerificationCodeSchedulingTasks {
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;

    public VerificationCodeSchedulingTasks(
            EmailVerificationCodeRepository emailVerificationCodeRepository,
            PasswordResetCodeRepository passwordResetCodeRepository
    ) {
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.passwordResetCodeRepository = passwordResetCodeRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        Instant now = Instant.now();

        emailVerificationCodeRepository.deleteAllByExpirationBefore(now);
        passwordResetCodeRepository.deleteAllByExpirationBefore(now);
    }
}
