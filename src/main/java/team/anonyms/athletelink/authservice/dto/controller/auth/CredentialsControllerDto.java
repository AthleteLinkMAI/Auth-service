package team.anonyms.athletelink.authservice.dto.controller.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CredentialsControllerDto(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
