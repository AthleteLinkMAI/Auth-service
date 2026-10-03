package team.anonyms.athletelink.authservice.dto.service.user;

public record RegisterRequestServiceDto(
        String username,
        String email,
        String password
) {}