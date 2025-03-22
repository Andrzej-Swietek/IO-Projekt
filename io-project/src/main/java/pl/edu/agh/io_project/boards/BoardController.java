package pl.edu.agh.io_project.boards;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/board")
@AllArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardRequest board) {
        return ResponseEntity.ok(boardService.createBoard(board));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Board>> getBoardsByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(boardService.getBoardsByProjectId(projectId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Board>> getBoardsByOwnerId(@PathVariable String ownerId) {
        return ResponseEntity.ok(boardService.getBoardsByOwnerId(ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody Board board) {
        return ResponseEntity.ok(boardService.updateBoard(id, board));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
