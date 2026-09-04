package team.anonyms.athletelink.authservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.entity.code.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetVerificationCode vc WHERE vc.expiration < :now")
    void deleteAllByExpirationBefore(Instant now);
}
