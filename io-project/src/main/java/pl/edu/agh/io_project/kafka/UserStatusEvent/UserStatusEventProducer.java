package pl.edu.agh.io_project.kafka.UserStatusEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.kafka.TaskEvent.TaskEvent;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatusEventProducer {
    private final KafkaTemplate<String, UserStatusChangedEvent> kafkaTemplate;
    private final String TOPIC_NAME = "user-status";

    public void sendUserStatusEvent(final UserStatusChangedEvent userStatusChangedEvent) {
        log.info("Producing user status event: {}", userStatusChangedEvent);
        Message<UserStatusChangedEvent> message = MessageBuilder
                .withPayload(userStatusChangedEvent)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .build();
        kafkaTemplate.send(message);
    }

    public void sendUserOnlineEvent(final String userId) {
        UserStatusChangedEvent event = new UserStatusChangedEvent(
                userId,
                UserStatusChangedEvent.UserStatus.ONLINE,
                Instant.now()
        );
        sendUserStatusEvent(event);
    }

    public void sendUserOfflineEvent(final String userId) {
        UserStatusChangedEvent event = new UserStatusChangedEvent(
                userId,
                UserStatusChangedEvent.UserStatus.OFFLINE,
                Instant.now()
        );
        sendUserStatusEvent(event);
    }
}
