package team.anonyms.athletelink.authorisation.dto.controller.authorisation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestControllerDto(
        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Invalid email format.")
        String email,

        @NotBlank(message = "Code cannot be blank.")
        @Size(min = 6, max = 6, message = "Code must be exactly 6 characters.")
        String code,

        @NotBlank(message = "New password cannot be blank.")
        @Size(min = 8, message = "Password must be at least 8 characters long.")
        String newPassword
) {}