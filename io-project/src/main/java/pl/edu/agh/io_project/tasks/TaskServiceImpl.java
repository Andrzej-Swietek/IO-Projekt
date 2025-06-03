package pl.edu.agh.io_project.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.boards.columns.BoardColumn;
import pl.edu.agh.io_project.boards.columns.BoardColumnRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final BoardColumnRepository columnRepository;

    @Override
    public List<Task> getAllTasks() {
        return this.taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) {
        return this.taskRepository.getReferenceById(id);
    }

    @Transactional
    public Task createTask(TaskRequest taskDTO) {
        BoardColumn column = columnRepository.findById(taskDTO.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        int lastPosition = taskRepository.findByColumnIdOrderByPosition(column.getId())
                .stream()
                .mapToInt(Task::getPosition)
                .max()
                .orElse(-1) + 1;

        Task task = Task.builder()
                .title(taskDTO.title())
                .description(taskDTO.description())
                .status(taskDTO.status())
                .column(column)
                .position(lastPosition)
//                .position(taskDTO.position())
                .build();

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, TaskRequest taskDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        var column = columnRepository.findById(taskDTO.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        task.setTitle(taskDTO.title());
        task.setDescription(taskDTO.description());
        task.setStatus(taskDTO.status());
        task.setColumn(column);
        task.setPosition(taskDTO.position());

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Task not found");
        }
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public void changeTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(status);
        taskRepository.save(task);
    }

    @Transactional
    public void assignUserToTask(Long taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.getAssignees().add(userId);
        taskRepository.save(task);
    }

    @Transactional
    public void reorderTasks(Long columnId, List<Long> taskIds) {
        List<Task> tasks = taskRepository.findByColumnIdOrderByPosition(columnId);
        if (tasks.size() != taskIds.size()) {
            throw new IllegalArgumentException("Task list size mismatch");
        }

        Map<Long, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, task -> task));

        IntStream.range(0, taskIds.size())
                .forEach(i -> {
                    Task task = Optional.ofNullable(taskMap.get(taskIds.get(i)))
                            .orElseThrow(() -> new IllegalArgumentException("Task not found in the specified column"));
                    task.setPosition(i);
                });

        taskRepository.saveAll(tasks);
    }

    @Override
    public List<Task> getTasksByUserId(String userId) {
        return taskRepository.findByAssigneesContaining(userId);
    }

    @Override
    public List<Task> getTasksByColumnId(Long columnId) {
        return taskRepository.findByColumnIdOrderByPosition(columnId);
    }
}
