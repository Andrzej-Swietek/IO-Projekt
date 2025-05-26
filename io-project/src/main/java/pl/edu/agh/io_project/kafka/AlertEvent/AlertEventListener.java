package pl.edu.agh.io_project.kafka.AlertEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlertEventListener {

    @KafkaListener(topics = "alert-events", groupId = "alert-consumer-group")
    public void listen(AlertEvent alertEvent) {
        log.info("Received AlertEvent: {}", alertEvent);
        // TODO: ...
    }
}