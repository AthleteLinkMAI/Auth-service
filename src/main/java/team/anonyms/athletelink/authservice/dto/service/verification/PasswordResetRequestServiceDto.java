package team.anonyms.athletelink.authservice.dto.service.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequestServiceDto(
        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Invalid email format.")
        String email,

        @NotBlank(message = "Code cannot be blank.")
        @Size(min = 6, max = 6, message = "Code must be exactly 6 characters.")
        String code
) {
}
