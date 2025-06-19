package pl.edu.agh.io_project.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.io_project.boards.columns.BoardColumn;
import pl.edu.agh.io_project.boards.columns.BoardColumnRepository;
import pl.edu.agh.io_project.tasks.events.TaskUpdatedEvent;
import pl.edu.agh.io_project.tasks.taskHistory.TaskHistoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final BoardColumnRepository columnRepository;
    private final ApplicationEventPublisher eventPublisher;

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
                .assignees(taskDTO.assignees())
                .build();

        Task saved = taskRepository.save(task);
        eventPublisher.publishEvent(new TaskUpdatedEvent(null, saved, getCurrentUserId()));
        return saved;
    }

    @Transactional
    public Task updateTask(Long taskId, TaskRequest taskDTO) {
        Task before = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Task previousCopy = deepCopy(before);

        BoardColumn column = columnRepository.findById(taskDTO.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        before.setTitle(taskDTO.title());
        before.setDescription(taskDTO.description());
        before.setStatus(taskDTO.status());
        before.setColumn(column);
        before.setPosition(taskDTO.position());
        before.setAssignees(taskDTO.assignees());

        Task saved = taskRepository.save(before);
        eventPublisher.publishEvent(new TaskUpdatedEvent(previousCopy, saved, getCurrentUserId()));

        return saved;
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        Task copy = deepCopy(task);
        eventPublisher.publishEvent(new TaskUpdatedEvent(copy, null, getCurrentUserId()));
        taskHistoryRepository.deleteByTaskId(taskId);

        taskRepository.delete(task);
    }

    @Transactional
    public void changeTaskStatus(Long taskId, TaskStatus status) {
        Task before = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        Task copy = deepCopy(before);

        before.setStatus(status);
        Task saved = taskRepository.save(before);
        eventPublisher.publishEvent(new TaskUpdatedEvent(copy, saved, getCurrentUserId()));
    }

    @Transactional
    public void assignUserToTask(Long taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Task before = deepCopy(task);
        task.getAssignees().add(userId);
        Task saved = taskRepository.save(task);
        eventPublisher.publishEvent(new TaskUpdatedEvent(before, saved, getCurrentUserId()));
    }

    @Transactional
    public void reorderTasks(Long columnId, List<Long> taskIds) {
        List<Task> tasks = taskRepository.findByColumnIdOrderByPosition(columnId);
        if (tasks.size() != taskIds.size()) {
            throw new IllegalArgumentException("Task list size mismatch");
        }

        Map<Long, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, task -> task));

        List<Task> changed = IntStream.range(0, taskIds.size())
                .mapToObj(i -> {
                    Task task = taskMap.get(taskIds.get(i));
                    if (task != null && task.getPosition() != i) {
                        Task before = deepCopy(task);
                        task.setPosition(i);
                        eventPublisher.publishEvent(new TaskUpdatedEvent(before, task, getCurrentUserId()));
                    }
                    return task;
                }).collect(Collectors.toList());

        taskRepository.saveAll(changed);
    }

    @Override
    public List<Task> getTasksByUserId(String userId) {
        return taskRepository.findByAssigneesContaining(userId);
    }

    @Override
    public List<Task> getTasksByColumnId(Long columnId) {
        return taskRepository.findByColumnIdOrderByPosition(columnId);
    }

    private Task deepCopy(Task original) {
        return Task.builder()
                .id(original.getId())
                .title(original.getTitle())
                .description(original.getDescription())
                .position(original.getPosition())
                .column(original.getColumn())
                .status(original.getStatus())
                .assignees(List.copyOf(original.getAssignees()))
                .labels(Set.copyOf(original.getLabels()))
                .build();
    }

    private String getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getSubject();
    }
}
