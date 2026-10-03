package team.anonyms.athletelink.authservice.dto.service.auth;

public record LoginResultDto (
        boolean success,
        String refreshToken
) {}
