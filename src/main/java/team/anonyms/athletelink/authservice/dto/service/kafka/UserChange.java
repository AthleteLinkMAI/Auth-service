package team.anonyms.athletelink.authservice.dto.service.kafka;

import team.anonyms.athletelink.authservice.dto.service.kafka.enums.UserChangeType;

import java.util.UUID;

public record UserChange(
        UserChangeType userChangeType,
        UUID userId
) {
}
