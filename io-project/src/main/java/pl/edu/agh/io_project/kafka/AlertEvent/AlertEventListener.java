package pl.edu.agh.io_project.kafka.AlertEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AlertEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "alert-events", groupId = "alert-consumer-group")
    public void listen(AlertEvent alertEvent) {
        log.info("Received AlertEvent: {}", alertEvent);

        switch (alertEvent) {
            case AlertOverloadedTeam overloadedTeamAlert -> notifyClientsViaWebsocket(overloadedTeamAlert);
            case AlertUserInactive userInactiveAlert -> notifyClientsViaWebsocket(userInactiveAlert);
            case AlertTaskStuck taskStuckAlert -> notifyClientsViaWebsocket(taskStuckAlert);
            default -> log.warn("Unknown alert event type: {}", alertEvent.getClass().getSimpleName());
        };

        // TODO: ...
    }

    private void notifyClientsViaWebsocket(AlertEvent alertEvent) {
        messagingTemplate.convertAndSend("/topic/alerts", alertEvent);
    }
}