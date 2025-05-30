package pl.edu.agh.io_project.kafka.TaskEvent;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@JsonTypeName("TaskCreated")
public record TaskCreated(
        String taskId,
        String title,
        String columnId,
        String creatorId,
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

