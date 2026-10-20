package team.anonyms.athletelink.authservice.dto.service.user;

import java.time.LocalDate;
import java.util.UUID;

public record UserUpdateServiceDto(
        UUID userId,
        String nickname,
        String name,
        String surname,
        LocalDate birthdate
) {
}
