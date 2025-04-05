package pl.edu.agh.io_project.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskGenerationResponse {
    private String title;
    private String description;
    private Integer priority;
    private Integer estimatedHours;
}