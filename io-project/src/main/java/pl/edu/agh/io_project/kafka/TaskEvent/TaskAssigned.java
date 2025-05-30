package pl.edu.agh.io_project.kafka.TaskEvent;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@JsonTypeName("TaskAssigned")
public record TaskAssigned(
        String taskId,
        String assigneeId,
        String teamId,
        String projectId,
        Instant timestamp
) implements TaskEvent {
    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
