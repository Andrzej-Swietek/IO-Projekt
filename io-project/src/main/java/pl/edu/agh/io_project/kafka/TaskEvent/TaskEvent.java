package pl.edu.agh.io_project.kafka.TaskEvent;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskCreated.class, name = "TaskCreated"),
        @JsonSubTypes.Type(value = TaskAssigned.class, name = "TaskAssigned"),
        @JsonSubTypes.Type(value = TaskClosed.class, name = "TaskClosed"),
        @JsonSubTypes.Type(value = TaskMoved.class, name = "TaskMoved"),
})
public sealed interface TaskEvent permits TaskCreated, TaskAssigned, TaskClosed, TaskMoved {
    String getTaskId();
    Instant getTimestamp();
}


