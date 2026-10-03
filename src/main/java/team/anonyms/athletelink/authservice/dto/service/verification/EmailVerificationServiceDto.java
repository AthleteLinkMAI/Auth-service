package team.anonyms.athletelink.authservice.dto.service.verification;

import java.util.UUID;

public record EmailVerificationServiceDto(
        String emailVerificationCode,
        UUID userId
) {
}
