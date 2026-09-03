package team.anonyms.athletelink.authservice.repository;

import team.anonyms.athletelink.authservice.entity.code.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findByUserId(UUID userId);
}
