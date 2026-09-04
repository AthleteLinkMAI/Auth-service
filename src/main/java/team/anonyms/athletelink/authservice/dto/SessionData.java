package team.anonyms.athletelink.authservice.dto;

import java.time.Instant;
import java.util.UUID;

public record SessionData(
        UUID sessionId,
        Instant expiration,
        Instant lastUsed,
        String userAgent,
        String ipAddress
) {
}
