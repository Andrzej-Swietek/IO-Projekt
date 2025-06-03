package pl.edu.agh.io_project.kafka.UserStatusEvent;

import java.time.Instant;

public record UserStatusChangedEvent(
        String userId,
        UserStatus status,
        Instant timestamp
) {

    public static enum UserStatus {
        ONLINE,
        OFFLINE
    }
}