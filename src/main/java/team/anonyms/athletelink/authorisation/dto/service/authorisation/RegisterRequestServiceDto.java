package team.anonyms.athletelink.authorisation.dto.service.authorisation;

public record RegisterRequestServiceDto(
        String username,
        String email,
        String password
) {}