package pl.edu.agh.io_project.kafka.AlertEvent;

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
) implements AlertEvent {}