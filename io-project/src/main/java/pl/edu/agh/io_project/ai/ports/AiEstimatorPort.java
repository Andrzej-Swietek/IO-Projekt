package pl.edu.agh.io_project.ai.ports;

import pl.edu.agh.io_project.tasks.estimate.Estimate;

public interface AiEstimatorPort {
    Estimate estimateTask(Long taskId, String taskDescription);
}