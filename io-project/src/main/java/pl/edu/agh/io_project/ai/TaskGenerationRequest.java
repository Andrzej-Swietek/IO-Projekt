package pl.edu.agh.io_project.ai;

import lombok.Builder;

@Builder
public record TaskGenerationRequest (
    String description,
    Long columnId,
    int position
) {}