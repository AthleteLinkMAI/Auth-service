package team.anonyms.athletelink.authservice.dto.controller.user;

import java.time.LocalDate;
import java.util.UUID;

public record UserPublicControllerDto(
        UUID id,
        String nickname,
        String name,
        String surname,
        Integer age,
        LocalDate registrationDate

) {
}
