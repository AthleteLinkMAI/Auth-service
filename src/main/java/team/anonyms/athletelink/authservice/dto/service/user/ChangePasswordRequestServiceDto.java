package team.anonyms.athletelink.authservice.dto.service.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChangePasswordRequestServiceDto(
        UUID userId,

        @NotBlank(message = "Old password cannot be blank")
        String oldPassword,

        @NotBlank(message = "New password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String newPassword
) {
}
