package pl.edu.agh.io_project.tasks;

import java.util.List;

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
