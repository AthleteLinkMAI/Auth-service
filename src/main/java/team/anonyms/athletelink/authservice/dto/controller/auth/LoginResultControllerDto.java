package team.anonyms.athletelink.authservice.dto.controller.auth;

import java.util.UUID;

public record LoginResultControllerDto(
        Boolean success,
        Boolean isVerified,
        UUID userId,
        String refreshToken
) {}