package pl.edu.agh.io_project.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstimateRequest {
    private String taskTitle;
    private String taskDescription;
}