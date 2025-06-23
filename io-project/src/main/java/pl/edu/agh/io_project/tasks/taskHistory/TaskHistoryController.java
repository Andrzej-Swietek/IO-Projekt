package pl.edu.agh.io_project.tasks.taskHistory;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task-history")
@AllArgsConstructor
public class TaskHistoryController {

    private final TaskHistoryService taskHistoryService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskHistory>> getTaskHistoryByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskHistoryService.getTaskHistoryByTaskId(taskId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskHistory> getTaskHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(taskHistoryService.getTaskHistoryById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskHistory>> getTaskHistoryForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskHistoryService.getTaskHistoryForUser(userId));
    }
}
