package team.anonyms.athletelink.authservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class KafkaConfiguration {
    @Bean
    public NewTopic userChangesTopic(KafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.userChanges()).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic notificationMessages(KafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.notificationMessages()).partitions(3).replicas(1).build();
    }
}
