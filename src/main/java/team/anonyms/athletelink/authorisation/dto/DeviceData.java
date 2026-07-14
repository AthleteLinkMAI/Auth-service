package team.anonyms.athletelink.authorisation.dto;

import java.time.Instant;
import java.util.UUID;

public record DeviceData(
        UUID deviceId,
        Instant expiration,
        Instant lastUsed,
        String userAgent,
        String ipAddress
) {
}
