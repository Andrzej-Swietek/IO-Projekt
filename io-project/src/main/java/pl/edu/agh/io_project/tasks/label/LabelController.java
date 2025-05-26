package pl.edu.agh.io_project.tasks.label;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.io_project.config.annotations.QueryBuilder;
import pl.edu.agh.io_project.config.annotations.QueryBuilderParams;
import pl.edu.agh.io_project.responses.PaginatedResponse;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/label")
public class LabelController {
// label service

//    @GetMapping("/all")
//    public PaginatedResponse<List<Label>> getAllLabels(@RequestParam String query) {
//        return ResponseEntity.ok(List.of());
//    }

}