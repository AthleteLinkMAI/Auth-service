package team.anonyms.athletelink.authservice.dto.service.user;

import java.util.UUID;

public record UserServiceDto(
        UUID id,
        String nickname,
        String username,
        String surname,
        String email,
        Boolean isVerified
) {
}
