package team.anonyms.athletelink.authservice.dto.service.user;

public record UserServiceDto(
        String username,
        String email,
        Boolean isVerified
) {
}
