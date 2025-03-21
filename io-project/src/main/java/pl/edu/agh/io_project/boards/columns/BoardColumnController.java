package pl.edu.agh.io_project.boards.columns;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/columns")
@RequiredArgsConstructor
public class BoardColumnController {

    private final BoardColumnService columnService;

    @PostMapping
    public ResponseEntity<BoardColumn> createColumn(@RequestBody BoardColumn column) {
        return ResponseEntity.ok(columnService.createColumn(column));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardColumn>> getColumnsByBoardId(@PathVariable Long boardId) {
        return ResponseEntity.ok(columnService.getColumnsByBoardId(boardId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardColumn> updateColumn(@PathVariable Long id, @RequestBody BoardColumn column) {
        return ResponseEntity.ok(columnService.updateColumn(id, column));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColumn(@PathVariable Long id) {
        columnService.deleteColumn(id);
        return ResponseEntity.noContent().build();
    }
}