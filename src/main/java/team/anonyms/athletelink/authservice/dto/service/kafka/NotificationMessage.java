package team.anonyms.athletelink.authservice.dto.service.kafka;

import team.anonyms.athletelink.authservice.dto.service.kafka.enums.NotificationType;

public record NotificationMessage(
        NotificationType notificationType,
        String notificationAddress,
        String subject,
        String text
) {
}
