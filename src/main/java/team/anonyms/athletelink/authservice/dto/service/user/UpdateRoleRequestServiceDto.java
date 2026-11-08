package team.anonyms.athletelink.authservice.dto.service.user;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.util.UUID;

public record UpdateRoleRequestServiceDto(
        UUID userId,
        UserCredentials.UserRoleType newRole
) {
}
