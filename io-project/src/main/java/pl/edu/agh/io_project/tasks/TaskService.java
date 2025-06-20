package pl.edu.agh.io_project.tasks;

import pl.edu.agh.io_project.users.UserPrincipal;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task createTask(TaskRequest task, UserPrincipal userPrincipal);

    Task updateTask(Long taskId, TaskRequest task, UserPrincipal userPrincipal);

    void deleteTask(Long taskId, UserPrincipal UserPrincipal);

    void changeTaskStatus(Long taskId, TaskStatus status, UserPrincipal userPrincipal);

    void assignUserToTask(Long taskId, String userId, UserPrincipal userPrincipal);

    void addLabelsToTask(Long taskId, List<Long> labels, UserPrincipal userPrincipal);

    void reorderTasks(Long columnId, List<Long> taskIds, UserPrincipal userPrincipal);

    List<Task> getTasksByUserId(String userId);

    List<Task> getTasksByColumnId(Long columnId);
}
