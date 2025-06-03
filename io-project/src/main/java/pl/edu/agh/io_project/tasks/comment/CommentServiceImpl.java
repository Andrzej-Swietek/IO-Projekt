package pl.edu.agh.io_project.tasks.comment;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.io_project.tasks.TaskRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    @Override
    public List<Comment> getCommentsByTask(Long taskId) {
        return this.commentRepository.findByTaskId(taskId);
    }

    @Override
    @Transactional
    public Comment getCommentById(Integer commentId) {
        return this.commentRepository.findById(commentId.longValue())
                .orElseThrow(()-> new IllegalStateException("Comment not found"));
    }

    @Override
    public Comment addComment(CommentRequest request) {
        var task = this.taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Comment comment = Comment.builder()
                .task(task)
                .authorId(request.authorId())
                .content(request.content())
                .build();


        return this.commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        this.commentRepository.deleteById(commentId.longValue());
    }

    @Override
    @Transactional
    public Comment updateComment(Integer commentId, CommentRequest request) {
        Comment comment = this.commentRepository.findById(commentId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        var task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        comment.setAuthorId(request.authorId());
        comment.setContent(request.content());
        comment.setTask(task);

        return commentRepository.save(comment);
    }
}
