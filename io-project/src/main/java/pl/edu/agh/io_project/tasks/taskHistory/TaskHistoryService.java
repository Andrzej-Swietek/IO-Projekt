package pl.edu.agh.io_project.tasks.taskHistory;

import java.util.List;

public interface TaskHistoryService {
    List<TaskHistory> getTaskHistoryByTaskId(Long taskId);

    TaskHistory getTaskHistoryById(Long id);

    List<TaskHistory> getTaskHistoryForUser(Long userId);
}
