package team.anonyms.athletelink.authservice.dto.controller.user;

import java.util.UUID;

public record UserControllerDto(
        UUID id,
        String nickname,
        String username,
        String surname,
        String email,
        Boolean isVerified
) {
}
