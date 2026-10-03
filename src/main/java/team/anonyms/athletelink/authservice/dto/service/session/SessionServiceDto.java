package team.anonyms.athletelink.authservice.dto.service.session;

import java.time.Instant;
import java.util.UUID;

public record SessionServiceDto(
        UUID sessionId,
        Instant expiration,
        Instant lastUsed,
        String userAgent,
        String ipAddress
) {
}
