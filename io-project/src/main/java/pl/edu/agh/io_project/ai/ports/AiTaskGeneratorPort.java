package pl.edu.agh.io_project.ai.ports;

import pl.edu.agh.io_project.tasks.Task;

import java.util.List;

public interface AiTaskGeneratorPort {
    Task generateTask(String taskDescription, Long columnId, int position);
    List<Task> generateMultipleTasks(String projectDescription, Long columnId, int count);
}