package pl.edu.agh.io_project.alerts.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.io_project.kafka.AlertEvent.AlertEvent;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertUserInactiveEntity extends AlertEntity {
    private String userId;
    private int daysInactive;

    public AlertUserInactiveEntity(
            String alertId,
            String message,
            AlertEvent.AlertSeverity severity,
            Instant timestamp,
            String userId,
            int daysInactive
    ) {
        super(alertId, message, severity, timestamp);
        this.userId = userId;
        this.daysInactive = daysInactive;
    }
}