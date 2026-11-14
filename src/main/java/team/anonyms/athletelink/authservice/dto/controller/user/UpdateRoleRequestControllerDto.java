package team.anonyms.athletelink.authservice.dto.controller.user;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

public record UpdateRoleRequestControllerDto(
        UUID userId,
        UserCredentials.UserRoleType newRole
) {
}
