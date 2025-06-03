package pl.edu.agh.io_project.tasks.comment;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable Long taskId) {
        List<Comment> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Integer commentId) {
        return ResponseEntity.ok(
                commentService.getCommentById(commentId)
        );
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest request) {
        Comment savedComment = commentService.addComment(request);
        return ResponseEntity.ok(savedComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(
                commentService.updateComment(commentId, request)
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
