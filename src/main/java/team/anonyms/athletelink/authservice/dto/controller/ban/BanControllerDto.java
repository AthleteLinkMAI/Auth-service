package team.anonyms.athletelink.authservice.dto.controller.ban;

import java.time.Instant;
import java.util.UUID;

public record BanControllerDto(
        UUID userId,
        Instant expiration,
        String reason,
        UUID bannedBy
) {
}
