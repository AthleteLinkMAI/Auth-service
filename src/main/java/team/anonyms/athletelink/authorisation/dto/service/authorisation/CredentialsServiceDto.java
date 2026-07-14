package team.anonyms.athletelink.authorisation.dto.service.authorisation;

public record CredentialsServiceDto(
        String email,
        String password,
        String userAgent,
        String userIpAddress
) {
}
