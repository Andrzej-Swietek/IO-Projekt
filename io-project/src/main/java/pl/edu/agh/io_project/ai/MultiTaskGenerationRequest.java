package pl.edu.agh.io_project.ai;

import lombok.Builder;

@Builder
public record MultiTaskGenerationRequest (
        String description,
        Long columnId,
        int count
) {}