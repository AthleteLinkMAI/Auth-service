package team.anonyms.athletelink.authservice.dto.controller.verification;

import java.util.UUID;

public record EmailVerificationControllerDto(
        String emailVerificationCode,
        UUID userId
) {
}
