package pl.edu.agh.io_project.ai;

import lombok.Builder;

@Builder
public record EstimateRequest(
        Long taskId,
        String taskTitle,
        String taskDescription
) {
}