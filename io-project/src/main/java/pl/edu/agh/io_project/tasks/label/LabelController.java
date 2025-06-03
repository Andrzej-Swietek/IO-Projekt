package pl.edu.agh.io_project.tasks.label;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/label")
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/all")
    public ResponseEntity<List<Label>> getAllLabels(@RequestParam Optional<String> query) {
        return ResponseEntity.ok(
                labelService.getAllLabels(query) // full text search
        );
    }

    @GetMapping("{labelId}")
    public ResponseEntity<Label> getLabelById(@PathVariable Integer labelId) {
        return ResponseEntity.ok(
                labelService.getLabelById(labelId)
        );
    }

    @GetMapping("/by-task/{taskId}")
    public ResponseEntity<List<Label>> getLabelsByTaskId(@PathVariable Integer taskId) {
        return ResponseEntity.ok(
                labelService.getLabelsByTask(taskId)
        );
    }

    @PostMapping
    public ResponseEntity<Label> createLabel(@Valid @RequestBody LabelRequest request) {
        return ResponseEntity.ok(
                labelService.addLabel(request)
        );
    }

    @PutMapping("/{labelId}")
    public ResponseEntity<Label> updateLabel(
            @PathVariable Integer labelId,
            @Valid @RequestBody LabelRequest request
    ) {
        return ResponseEntity.ok(
                labelService.updateLabel(labelId, request)
        );
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<Label> deleteLabel(
            @PathVariable Integer labelId
    ) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }
}