package team.anonyms.athletelink.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.anonyms.athletelink.authservice.entity.Ban;

import java.util.UUID;

@Repository
public interface BanRepository extends JpaRepository<Ban, UUID> {
}
