package team.anonyms.athletelink.authorisation.repositories;

import team.anonyms.athletelink.authorisation.entities.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserId(UUID userId);
}
