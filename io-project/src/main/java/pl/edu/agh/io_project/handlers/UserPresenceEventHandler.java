package pl.edu.agh.io_project.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pl.edu.agh.io_project.kafka.UserStatusEvent.UserStatusEventProducer;

@Component
@RequiredArgsConstructor
public class UserPresenceEventHandler {

    private final UserStatusEventProducer producer;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        String userId = extractUserId(event);
        if (userId != null) {
            producer.sendUserOnlineEvent(userId);
        }
    }


    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        String userId = extractUserId(event);
        if (userId != null) {
            producer.sendUserOfflineEvent(userId);
        }
    }

    private String extractUserId(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        return accessor.getUser() != null ? accessor.getUser().getName() : null;
    }
}