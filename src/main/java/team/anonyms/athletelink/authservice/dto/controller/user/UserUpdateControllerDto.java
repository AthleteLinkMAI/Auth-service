package team.anonyms.athletelink.authservice.dto.controller.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateControllerDto(
        @Size(max = 30)
        @NotBlank
        String username
) {
}
