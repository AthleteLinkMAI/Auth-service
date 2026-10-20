package team.anonyms.athletelink.authservice.dto.service.user;

import java.time.LocalDate;
import java.util.UUID;

public record UserPublicServiceDto(
        UUID id,
        String nickname,
        String name,
        String surname,
        Integer age,
        LocalDate registrationDate
) {
}
