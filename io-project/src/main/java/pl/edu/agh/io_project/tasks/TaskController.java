package pl.edu.agh.io_project.tasks;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io_project.ai.EstimateRequest;
import pl.edu.agh.io_project.ai.MultiTaskGenerationRequest;
import pl.edu.agh.io_project.ai.TaskGenerationRequest;
import pl.edu.agh.io_project.ai.ports.AiEstimatorPort;
import pl.edu.agh.io_project.ai.ports.AiTaskGeneratorPort;
import pl.edu.agh.io_project.tasks.estimate.Estimate;
import pl.edu.agh.io_project.users.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final AiTaskGeneratorPort aiTaskGeneratorPort;
    private final AiEstimatorPort aiEstimatorPort;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/by-column/{columnId}")
    public ResponseEntity<List<Task>> getTasksByColumnId(@PathVariable Long columnId) {
        List<Task> tasks = taskService.getTasksByColumnId(columnId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable String userId) {
        List<Task> tasks = taskService.getTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody TaskRequest taskRequest
    ) {
        Task task = taskService.createTask(taskRequest, user);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestBody TaskRequest taskRequest
    ) {
        Task task = taskService.updateTask(id, taskRequest, user);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id
    ) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeTaskStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestBody TaskStatus status
    ) {
        taskService.changeTaskStatus(id, status, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<Void> assignUserToTask(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestParam String userId
    ) {
        taskService.assignUserToTask(id, userId, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/add-labels/{taskId}")
    public ResponseEntity<Void> addLabelsToTask(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long taskId,
            @RequestBody List<Long> labels
    ) {
        taskService.addLabelsToTask(taskId, labels, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reorder-tasks/{columnId}")
    public ResponseEntity<Void> reorderTasks(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long columnId,
            @RequestBody List<Long> taskIdsInNewOrder
    ) {
        taskService.reorderTasks(columnId, taskIdsInNewOrder, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task-estimate")
    public ResponseEntity<Estimate> estimateTask(@RequestBody EstimateRequest estimateRequest) {
        Estimate estimate = aiEstimatorPort.estimateTask(estimateRequest.taskId(), estimateRequest.taskDescription());
        return ResponseEntity.ok(estimate);
    }

    @PostMapping("/generate")
    public ResponseEntity<Task> generateTask(@RequestBody TaskGenerationRequest request) {
        Task task = aiTaskGeneratorPort.generateTask(request.description(), request.columnId(), request.position());
        return ResponseEntity.ok(task);
    }

    @PostMapping("/generate-multiple")
    public ResponseEntity<List<Task>> generateMultipleTasks(@RequestBody MultiTaskGenerationRequest request) {
        List<Task> tasks = aiTaskGeneratorPort.generateMultipleTasks(
                request.description(),
                request.columnId(),
                request.count()
        );
        return ResponseEntity.ok(tasks);
    }
}
