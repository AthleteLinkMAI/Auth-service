package team.anonyms.athletelink.authservice.dto.controller.user;

import java.util.UUID;

public record UserPublicControllerDto(
        UUID id,
        String nickname,
        String username,
        String surname
) {
}
