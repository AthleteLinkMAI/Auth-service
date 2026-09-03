package team.anonyms.athletelink.authservice.repository;

import team.anonyms.athletelink.authservice.entity.code.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findByUserId(UUID userId);
}
