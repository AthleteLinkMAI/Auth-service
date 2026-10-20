package team.anonyms.athletelink.authservice.dto.controller.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequestControllerDto(
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @NotBlank(message = "Username cannot be blank")
        String nickname,

        @Size(max = 30)
        @Nullable
        String name,

        @Size(max = 30)
        @Nullable
        String surname,

        @Email(message = "Invalid email format.")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        @NotBlank(message = "Password cannot be blank")
        String password,

        @NotNull(message = "Birthdate cannot be blank")
        LocalDate birthdate
) {}