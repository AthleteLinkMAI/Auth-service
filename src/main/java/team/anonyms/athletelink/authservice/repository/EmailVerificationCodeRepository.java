package team.anonyms.athletelink.authservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.entity.code.EmailCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailCode, Long> {
    Optional<EmailCode> findByUserId(UUID userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM EmailCode vc WHERE vc.expiration < :now")
    void deleteAllByExpirationBefore(Instant now);
}
