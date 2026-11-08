package team.anonyms.athletelink.authservice.dto.service.ban;

import java.time.Instant;
import java.util.UUID;

public record BanServiceDto(
        UUID userId,
        Instant expiration,
        String reason,
        UUID bannedBy
) {
}
