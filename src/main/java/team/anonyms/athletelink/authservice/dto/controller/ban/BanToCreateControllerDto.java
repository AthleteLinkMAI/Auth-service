package team.anonyms.athletelink.authservice.dto.controller.ban;

import java.time.Instant;
import java.util.UUID;

public record BanToCreateControllerDto(
        UUID userToBan,
        String reason,
        Instant expiration
) {
}
