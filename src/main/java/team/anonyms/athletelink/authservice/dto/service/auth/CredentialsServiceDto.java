package team.anonyms.athletelink.authservice.dto.service.auth;

public record CredentialsServiceDto(
        String email,
        String password,
        String userAgent,
        String userIpAddress
) {
}
