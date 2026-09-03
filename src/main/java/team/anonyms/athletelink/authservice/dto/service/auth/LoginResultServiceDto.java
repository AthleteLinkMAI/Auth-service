package team.anonyms.athletelink.authservice.dto.service.auth;

import java.util.UUID;

public record LoginResultServiceDto(
        Boolean success,
        Boolean isVerified,
        UUID userId,
        String refreshToken
) {}