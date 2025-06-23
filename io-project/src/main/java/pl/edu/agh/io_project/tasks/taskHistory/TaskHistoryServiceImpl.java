package pl.edu.agh.io_project.tasks.taskHistory;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    @Override
    @Transactional
    public List<TaskHistory> getTaskHistoryByTaskId(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByTimestampDesc(taskId);
    }

    @Override
    @Transactional
    public TaskHistory getTaskHistoryById(Long id) {
        return taskHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task history not found"));
    }

    @Override
    @Transactional
    public List<TaskHistory> getTaskHistoryForUser(Long userId) {
        return taskHistoryRepository.findAll().stream()
                .filter(h -> h.getUser().equals(String.valueOf(userId)))
                .toList();
    }
}
