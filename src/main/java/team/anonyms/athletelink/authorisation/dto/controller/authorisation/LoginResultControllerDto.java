package team.anonyms.athletelink.authorisation.dto.controller.authorisation;

import java.util.UUID;

public record LoginResultControllerDto(
        Boolean success,
        Boolean isVerified,
        UUID userId,
        String accessToken
) {}