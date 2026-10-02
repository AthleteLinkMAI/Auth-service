package team.anonyms.athletelink.authservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.athletelink.authservice.entity.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, UUID> {
    List<JwtRefreshToken> findAllByUserId(UUID userId);

    Optional<JwtRefreshToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM JwtRefreshToken token WHERE token.expiration < :now")
    void deleteAllByExpirationBefore(Instant now);
}
