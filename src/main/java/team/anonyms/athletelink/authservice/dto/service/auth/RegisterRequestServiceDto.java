package team.anonyms.athletelink.authservice.dto.service.auth;

public record RegisterRequestServiceDto(
        String username,
        String email,
        String password
) {}