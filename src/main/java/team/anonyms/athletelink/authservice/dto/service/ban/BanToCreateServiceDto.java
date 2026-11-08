package team.anonyms.athletelink.authservice.dto.service.ban;

import team.anonyms.athletelink.authservice.entity.UserCredentials;

import java.time.Instant;
import java.util.UUID;

public record BanToCreateServiceDto(
        UUID userId,
        UserCredentials.UserRoleType role,
        UUID userToBan,
        String reason,
        Instant expiration
) {
}
