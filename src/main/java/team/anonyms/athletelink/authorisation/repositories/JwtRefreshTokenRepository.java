package team.anonyms.athletelink.authorisation.repositories;

import team.anonyms.athletelink.authorisation.entities.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, UUID> {
    List<JwtRefreshToken> findAllByUserId(UUID userId);
}
