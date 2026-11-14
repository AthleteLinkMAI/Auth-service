package team.anonyms.athletelink.authservice.dto.service.user;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

public record UserCredentialsServiceDto(
        UUID id,
        UserCredentials.UserRoleType role,
        String email,
        Boolean isVerified,
        Boolean isBanned
) {
}
