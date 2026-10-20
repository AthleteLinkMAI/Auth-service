package team.anonyms.athletelink.authservice.dto.service.user;

import java.time.LocalDate;

public record RegisterRequestServiceDto(
        String nickname,
        String name,
        String surname,
        String email,
        String password,
        LocalDate birthdate
) {}