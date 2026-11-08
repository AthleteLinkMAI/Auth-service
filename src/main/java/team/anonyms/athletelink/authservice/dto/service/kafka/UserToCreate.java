package team.anonyms.athletelink.authservice.dto.service.kafka;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.time.LocalDate;
import java.util.UUID;

public record UserToCreate(
        UUID userId,
        UserCredentials.UserRoleType role,
        String nickname,
        String name,
        String surname,
        LocalDate birthdate,
        String email,
        Boolean isVerified,
        Boolean isBanned
) {
}
