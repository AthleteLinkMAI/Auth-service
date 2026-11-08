package team.anonyms.athletelink.authservice.repository;

import org.springframework.stereotype.Repository;
import team.anonyms.athletelink.authservice.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {
    Optional<UserCredentials> findByEmail(String email);
}
