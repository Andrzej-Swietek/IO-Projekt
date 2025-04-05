package pl.edu.agh.io_project.ai;

import lombok.Builder;

@Builder
public record EstimateResponse(
        Integer estimatedHours
) {
}