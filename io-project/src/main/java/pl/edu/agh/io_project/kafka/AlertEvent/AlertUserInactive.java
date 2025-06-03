package pl.edu.agh.io_project.kafka.AlertEvent;

import java.time.Instant;

public record AlertUserInactive(
        String alertId,
        String userId,
        int daysInactive,
        String message,
        AlertSeverity severity,
        Instant timestamp
) implements AlertEvent {}