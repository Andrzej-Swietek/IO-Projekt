package pl.edu.agh.io_project.kafka.AlertEvent;

import com.fasterxml.jackson.annotation.*;
import java.time.Instant;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AlertTaskStuck.class, name = "AlertTaskStuck"),
        @JsonSubTypes.Type(value = AlertUserInactive.class, name = "AlertUserInactive"),
        @JsonSubTypes.Type(value = AlertOverloadedTeam.class, name = "AlertOverloadedTeam")
})
public interface AlertEvent {
    String alertId();
    String message();
    Instant timestamp();
    AlertSeverity severity();

    public static enum AlertSeverity {
        Info, Warning, Critical, Message
    }
}