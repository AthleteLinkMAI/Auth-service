package team.anonyms.athletelink.authservice.dto.service.user;

import java.util.UUID;

public record UserPublicServiceDto(
        UUID id,
        String nickname,
        String username,
        String surname
) {
}
