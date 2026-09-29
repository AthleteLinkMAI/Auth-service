package team.anonyms.athletelink.authservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.kafka.topics")
public record KafkaTopicsProperties(
    String userChanges,
    String notificationMessages
) {
}
