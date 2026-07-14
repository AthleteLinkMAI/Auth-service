package team.anonyms.athletelink.authorisation.repositories;

import team.anonyms.athletelink.authorisation.entities.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
}
