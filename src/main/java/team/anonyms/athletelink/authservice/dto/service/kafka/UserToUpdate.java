package team.anonyms.athletelink.authservice.dto.service.kafka;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

public record UserToUpdate(
        UUID userId,
        UserCredentials.UserRoleType role,
        String email,
        Boolean isVerified,
        Boolean isBanned
) {
}
