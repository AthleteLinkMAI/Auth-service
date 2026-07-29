package team.anonyms.athletelink.authorisation.repositories;

import team.anonyms.athletelink.authorisation.entities.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, UUID> {
    List<JwtRefreshToken> findAllByUserId(UUID userId);

    Optional<JwtRefreshToken> findByToken(String token);
}
