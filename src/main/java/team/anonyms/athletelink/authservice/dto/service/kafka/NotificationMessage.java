package team.anonyms.athletelink.authservice.dto.service.kafka;

import team.anonyms.athletelink.authservice.dto.service.kafka.enums.NotificationType;

import java.util.Map;

public record NotificationMessage(
        Map<NotificationType, String> receiverCredentialsPerDesiredNotificationType,
        String subject,
        String text
) {
}
