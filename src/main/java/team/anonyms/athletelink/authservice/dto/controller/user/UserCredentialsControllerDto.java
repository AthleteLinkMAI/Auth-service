package team.anonyms.athletelink.authservice.dto.controller.user;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

public record UserCredentialsControllerDto(
        UUID id,
        UserCredentials.UserRoleType role,
        String email,
        Boolean isVerified,
        Boolean isBanned
) {
}
