package team.anonyms.athletelink.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import team.anonyms.athletelink.authservice.config.kafka.KafkaTopicsProperties;
import team.anonyms.athletelink.authservice.dto.service.kafka.NotificationMessage;
import team.anonyms.athletelink.authservice.dto.service.kafka.UserChange;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<UUID, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void sendNotificationMessage(NotificationMessage notificationMessage, UUID userId) {
        kafkaTemplate.send(topics.notificationMessages(), userId, notificationMessage);
    }

    public void sendUserChange(UserChange userChange) {
        kafkaTemplate.send(topics.userChanges(), userChange.userId(), userChange);
    }
}
