package team.anonyms.athletelink.authservice.dto.controller.user;

import java.util.UUID;

public record UserCredentialsControllerDto(
        UUID id,
        String email,
        Boolean isVerified
) {
}
