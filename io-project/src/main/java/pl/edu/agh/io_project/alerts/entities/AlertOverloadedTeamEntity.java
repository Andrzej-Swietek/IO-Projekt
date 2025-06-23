package pl.edu.agh.io_project.alerts.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.io_project.kafka.AlertEvent.AlertEvent;

import java.time.Instant;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertOverloadedTeamEntity extends AlertEntity {
    private String teamId;
    private String projectId;

    @ElementCollection
    private Set<String> overloadedMembers;

    private int maxRecommended;

    public AlertOverloadedTeamEntity(
            String alertId,
            String message,
            AlertEvent.AlertSeverity severity,
            Instant timestamp,
            String teamId,
            String projectId,
            Set<String> overloadedMembers,
            int maxRecommended
    ) {
        super(alertId, message, severity, timestamp);
        this.teamId = teamId;
        this.projectId = projectId;
        this.overloadedMembers = overloadedMembers;
        this.maxRecommended = maxRecommended;
    }
}