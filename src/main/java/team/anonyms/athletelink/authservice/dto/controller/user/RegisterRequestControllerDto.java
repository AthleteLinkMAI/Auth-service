package team.anonyms.athletelink.authservice.dto.controller.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestControllerDto(
        @NotBlank(message = "Username cannot be blank.")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters.")
        String username,

        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Invalid email format.")
        String email,

        @NotBlank(message = "Password cannot be blank.")
        @Size(min = 8, message = "Password must be at least 8 characters long.")
        String password
) {}