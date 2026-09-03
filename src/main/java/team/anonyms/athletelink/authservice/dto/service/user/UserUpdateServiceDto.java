package team.anonyms.athletelink.authservice.dto.service.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserUpdateServiceDto(
        UUID userId,

        @Size(max = 30)
        @NotBlank
        String username
) {
}
