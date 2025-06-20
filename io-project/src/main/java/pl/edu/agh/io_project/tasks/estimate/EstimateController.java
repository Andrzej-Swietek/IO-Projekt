package pl.edu.agh.io_project.tasks.estimate;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/task-estimates")
public class EstimateController {
    private final EstimateService estimateService;

    @PostMapping
    public ResponseEntity<Estimate> createEstimate(@RequestBody Estimate estimate) {
        Estimate created = estimateService.createEstimate(estimate);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estimate> getEstimateById(@PathVariable Long id) {
        Estimate estimate = estimateService.getEstimateById(id);
        return ResponseEntity.ok(estimate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estimate> updateEstimate(@PathVariable Long id, @RequestBody Estimate estimate) {
        Estimate updated = estimateService.updateEstimate(id, estimate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstimate(@PathVariable Long id) {
        estimateService.deleteEstimate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Estimate>> getAllEstimates() {
        return ResponseEntity.ok(estimateService.getAllEstimates());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Estimate>> getEstimatesByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(estimateService.getEstimatesByTaskId(taskId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Estimate>> getEstimatesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(estimateService.getEstimatesByUserId(userId));
    }
}
