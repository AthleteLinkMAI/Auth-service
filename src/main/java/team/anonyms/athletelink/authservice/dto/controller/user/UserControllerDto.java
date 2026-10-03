package team.anonyms.athletelink.authservice.dto.controller.user;

public record UserControllerDto(
        String username,
        String email,
        Boolean isVerified
) {
}
