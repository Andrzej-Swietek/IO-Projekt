package pl.edu.agh.io_project.kafka.AlertEvent;

import pl.edu.agh.io_project.alerts.entities.AlertUserInactiveEntity;

import java.time.Instant;

public record AlertUserInactive(
        String alertId,
        String userId,
        int daysInactive,
        String message,
        AlertSeverity severity,
        Instant timestamp
) implements AlertEvent {
    public AlertUserInactiveEntity toEntity() {
        return new AlertUserInactiveEntity(
                alertId,
                message,
                severity,
                timestamp,
                userId,
                daysInactive
        );
    }
}