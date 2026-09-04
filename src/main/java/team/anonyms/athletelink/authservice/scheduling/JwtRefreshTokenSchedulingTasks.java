package team.anonyms.athletelink.authservice.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.athletelink.authservice.repository.JwtRefreshTokenRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class JwtRefreshTokenSchedulingTasks {
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    public JwtRefreshTokenSchedulingTasks(JwtRefreshTokenRepository jwtRefreshTokenSchedulingTasks) {
        this.jwtRefreshTokenRepository = jwtRefreshTokenSchedulingTasks;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void deleteAllExpiredJwtRefreshTokens() {
        jwtRefreshTokenRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
