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
public class AlertTaskStuckEntity extends AlertEntity {
    private String taskId;
    private String title;
    private String userId;
    private int daysStuck;

    public AlertTaskStuckEntity(
            String alertId,
            String message,
            AlertEvent.AlertSeverity severity,
            Instant timestamp,
            String taskId,
            String title,
            String userId,
            int daysStuck
    ) {
        super(alertId, message, severity, timestamp);
        this.taskId = taskId;
        this.title = title;
        this.userId = userId;
        this.daysStuck = daysStuck;
    }
}