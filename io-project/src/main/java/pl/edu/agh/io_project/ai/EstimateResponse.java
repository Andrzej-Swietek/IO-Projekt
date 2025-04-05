package pl.edu.agh.io_project.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstimateResponse {
    private Integer estimatedHours;
    private String reasoning;
}