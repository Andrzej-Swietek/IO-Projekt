package pl.edu.agh.io_project.kafka.TaskEvent;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@JsonTypeName("TaskMoved")
public record TaskMoved(
        String taskId,
        String teamId,
        String fromColumnId,
        String toColumnId,
        String movedBy,
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