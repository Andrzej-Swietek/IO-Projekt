package pl.edu.agh.io_project.kafka.AlertEvent;

import pl.edu.agh.io_project.alerts.entities.AlertTaskStuckEntity;

import java.time.Instant;

public record AlertTaskStuck(
        String alertId,
        String taskId,
        String title,
        String userId,
        int daysStuck,
        String message,
        AlertSeverity severity,
        Instant timestamp
) implements AlertEvent {
    public AlertTaskStuckEntity toEntity() {
        return new AlertTaskStuckEntity(
                alertId,
                message,
                severity,
                timestamp,
                taskId,
                title,
                userId,
                daysStuck
        );
    }
}