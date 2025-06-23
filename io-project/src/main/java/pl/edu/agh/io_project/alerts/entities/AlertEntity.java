package pl.edu.agh.io_project.alerts.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.io_project.kafka.AlertEvent.AlertEvent;

import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AlertEntity {
    @Id
    private String alertId;

    private String message;

    @Enumerated(EnumType.STRING)
    private AlertEvent.AlertSeverity severity;

    private Instant timestamp;
}