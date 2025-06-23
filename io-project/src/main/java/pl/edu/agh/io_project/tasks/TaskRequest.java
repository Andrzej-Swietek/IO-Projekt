package pl.edu.agh.io_project.tasks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskRequest(
        String title,
        String description,
        Long columnId,
        Integer priority,
        Integer position,
        TaskStatus status,
        List<Long> labelIds,
        List<String> assignees
) {
}
