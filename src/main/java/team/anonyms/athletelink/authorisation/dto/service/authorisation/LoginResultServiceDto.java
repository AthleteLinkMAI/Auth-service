package team.anonyms.athletelink.authorisation.dto.service.authorisation;

import java.util.UUID;

public record LoginResultServiceDto(
        Boolean success,
        Boolean isVerified,
        UUID userId,
        String accessToken,
        String refreshToken
) {}