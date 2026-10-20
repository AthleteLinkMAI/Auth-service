package team.anonyms.athletelink.authservice.dto.service.user;

import java.time.LocalDate;
import java.util.UUID;

public record UserServiceDto(
        UUID id,
        String nickname,
        String name,
        String surname,
        String email,
        Boolean isVerified,
        Integer age,
        LocalDate registrationDate
) {
}
