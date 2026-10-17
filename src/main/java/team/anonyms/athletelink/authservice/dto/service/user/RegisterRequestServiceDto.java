package team.anonyms.athletelink.authservice.dto.service.user;

public record RegisterRequestServiceDto(
        String nickname,
        String username,
        String surname,
        String email,
        String password
) {}