package pl.edu.agh.io_project.tasks;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task createTask(TaskRequest task);

    Task updateTask(Long taskId, TaskRequest task);

    void deleteTask(Long taskId);

    void changeTaskStatus(Long taskId, TaskStatus status);

    void assignUserToTask(Long taskId, String userId);

    void addLabelsToTask(Long taskId, List<Long> labels);

    void reorderTasks(Long columnId, List<Long> taskIds);

    List<Task> getTasksByUserId(String userId);

    List<Task> getTasksByColumnId(Long columnId);
}
