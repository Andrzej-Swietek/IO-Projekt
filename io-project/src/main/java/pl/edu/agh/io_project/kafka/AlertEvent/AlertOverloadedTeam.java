package pl.edu.agh.io_project.kafka.AlertEvent;

import java.time.Instant;
import java.util.Set;

public record AlertOverloadedTeam(
        String alertId,
        String teamId,
        String projectId,
        Set<String> overloadedMembers,
        int maxRecommended,
        String message,
        AlertSeverity severity,
        Instant timestamp
) implements AlertEvent {}